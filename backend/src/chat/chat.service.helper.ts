import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ChatEntity, MessageEntity, User } from '../typeorm/entities';
import { Repository } from 'typeorm';
import {
  catchError,
  forkJoin,
  from,
  map,
  mergeMap,
  Observable,
  of,
  switchMap,
  throwError,
} from 'rxjs';
import { CreateMessageDto } from './dto/create-message.dto';
import { LocalFileDto } from '../local-file/dto/local-file.dto';
import { LocalFileService } from '../local-file/local-file.service';
import { LocalFileEntity } from '../typeorm/entities/local-file.entity';
import { ImageEntity } from "../typeorm/entities/image.entity";

@Injectable()
export class ChatServiceHelper {
  constructor(
    @InjectRepository(ChatEntity)
    private readonly chatRepository: Repository<ChatEntity>,
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
    @InjectRepository(MessageEntity)
    private readonly messagesRepository: Repository<MessageEntity>,
    private readonly localFileService: LocalFileService,
  ) {}

  createChat(
    creatorUserId: string,
    friendUserId: string,
  ): Observable<ChatEntity> {
    if (creatorUserId == friendUserId) {
      throw new BadRequestException(
        `Creator user ${creatorUserId} is the same as friend user ${friendUserId}`,
      );
    }

    const creatorUser$ = this.userRepository.findOneByOrFail({
      id: creatorUserId,
    });
    const friendUser$ = this.userRepository.findOneByOrFail({
      id: friendUserId,
    });

    return forkJoin([creatorUser$, friendUser$]).pipe(
      switchMap(([creatorUser, friendUser]) => {
        if (!creatorUser) {
          throw new NotFoundException('Creator user does not exist');
        }

        if (!friendUser) {
          throw new NotFoundException('Friend user does not exist');
        }

        return this.getChat(creatorUserId, friendUserId).pipe(
          switchMap((chat: ChatEntity) => {
            if (!chat) {
              // @ts-ignore
              const newChat: ChatEntity = {
                users: [creatorUser, friendUser],
              };
              return from(this.chatRepository.save(newChat)).pipe();
            }
            return of(chat);
          }),
        );
      }),
      catchError((error) => throwError(error)),
    );
  }

  getAllChats(user: User, size = 10, page = 1) {
    const skip = (page - 1) * size;
    const userId = user.id;

    const messagesQuery = this.messagesRepository
      .createQueryBuilder('messages')
      .orderBy('messages.created_at', 'DESC')
      .limit(1);

    return from(
      this.chatRepository
        .createQueryBuilder('chat')
        .leftJoin('chat.users', 'user')
        .where('user.id = :userId', { userId })
        .leftJoinAndSelect(
          'chat.messages',
          'messages',
          'messages.id = (SELECT id FROM messages WHERE messages.chatId = chat.id ORDER BY created_at DESC LIMIT 1)',
        )
        .leftJoinAndSelect('messages.author', 'author')
        .leftJoinAndSelect('author.avatar', 'author_avatar')
        .leftJoinAndSelect('messages.images', 'images')
        .leftJoinAndSelect(
          'chat.users',
          'all_users',
          'all_users.id != :userId',
          { userId },
        )
        .leftJoinAndSelect('all_users.avatar', 'avatar')
        .orderBy('messages.created_at', 'DESC')
        .take(size)
        .skip(skip)
        .getMany(),
    );
  }

  removeChat(chatId: string) {
    return from(
      this.chatRepository
      .findOne({
        relations: { users: true },
        where: { id: chatId }
      })
      .then((itemToRemove) => {
        if (!itemToRemove) {
          return undefined;
        }

        this.chatRepository.remove(itemToRemove);
        return itemToRemove
      })
    );
  }

  createMessage(user: User, dto: CreateMessageDto, images: LocalFileDto[]) {
    if (!dto.chatId && !dto.friendId) {
      throw new BadRequestException('Chat id or friend id is required');
    }

    let observer: Observable<string>;

    if (!dto.chatId) {
      observer = this.createChat(user.id, dto.friendId).pipe(
        map((chatEntity: ChatEntity) => chatEntity.id),
      );
    } else {
      observer = of(dto.chatId);
    }

    return observer.pipe(
      mergeMap((chatId: string) => {
        return from(this.getAllUsersFromChat(chatId)).pipe(
          switchMap((chat: ChatEntity) => {
            const u = chat.users.find((u) => u.id === user.id);
            if (u) {
              return of(chatId);
            } else {
              throw new BadRequestException('You do not belong to this chat');
            }
          }),
        );
      }),
      switchMap((chatId: string) => {
        return this.localFileService.saveListOfImages(images).pipe(
          map((items: ImageEntity[]) => {
            return this.messagesRepository.create({
              ...dto,
              chat: { id: chatId },
              author: { id: user.id },
              images: items,
            });
          }),
        );
      }),
      switchMap((message: MessageEntity) => {
        return this.saveAndRetrieveMessage(user, message);
      }),
    );
  }

  private saveAndRetrieveMessage(
    user: User,
    msg: MessageEntity,
  ): Observable<MessageEntity> {
    return from(this.messagesRepository.save(msg)).pipe(
      switchMap((m: MessageEntity) => this.getMessageById(m.id)),
    );
  }

  getMessageById(messageId: string) {
    return from(
      this.messagesRepository.createQueryBuilder('message')
      .where('message.id = :messageId', { messageId: messageId })
      .leftJoinAndSelect('message.author', 'author')
      .leftJoinAndSelect('message.images', 'images')
      .leftJoinAndSelect('author.avatar', 'avatar')
      .getOne()
    );
  }

  getAllMessage(chatId: string, size = 10, page = 1) {
    const skip = (page - 1) * size;
    return from(
      this.messagesRepository
        .createQueryBuilder('message')
        .leftJoin('message.chat', 'chat')
        .where('chat.id = :chatId', { chatId })
        .leftJoinAndSelect('message.author', 'user')
        .leftJoinAndSelect('user.avatar', 'avatar')
        .leftJoinAndSelect('message.images', 'images')
        .orderBy('message.created_at', 'DESC')
        .take(size)
        .skip(skip)
        .getMany(),
    ).pipe();
  }

  async getAllUsersFromChat(chatId: string) {
    return this.chatRepository.findOne({
      select: ['users'],
      where: {
        id: chatId,
      },
      relations: { users: true },
    });
  }

  private getChat(
    creatorId: string,
    friendId: string,
  ): Observable<ChatEntity | undefined> {
    return from(
      this.chatRepository
        .createQueryBuilder('chats')
        .leftJoin('chats.users', 'user')
        .where('user.id = :creatorId', { creatorId })
        .orWhere('user.id = :friendId', { friendId })
        .groupBy('chats.id')
        .having('COUNT(*) > 1')
        .getOne(),
    ).pipe(map((conversation: ChatEntity) => conversation || undefined));
  }
}
