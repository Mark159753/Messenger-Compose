import { Injectable } from '@nestjs/common';
import { UpdateUserDto } from './dto/update-user.dto';
import { Brackets, Repository } from 'typeorm';
import { ChatEntity, User } from '../typeorm/entities';
import { InjectRepository } from '@nestjs/typeorm';
import { from, map, mapTo, switchMap } from 'rxjs';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User)
    private readonly repository: Repository<User>,
    @InjectRepository(ChatEntity)
    private readonly chatRepository: Repository<ChatEntity>,
  ) {}
  findOne(userId: string) {
    return this.repository.findOne({
      where: { id: userId },
      relations: ['avatar'],
    });
  }

  async search(user: User, query: string, size = 10, page = 1) {
    const userId = user.id;
    const skip = (page - 1) * size;
    return from(
      this.chatRepository
        .createQueryBuilder('chat')
        .leftJoin('chat.users', 'users')
        .where('users.id = :userId', { userId: userId })
        .getMany(),
    ).pipe(
      map((chat: ChatEntity[]) => chat.map((chat: ChatEntity) => chat.id)),
      switchMap((chatsId: string[]) => {
        let joinCondition: string;
        if (chatsId.length != 0) {
          joinCondition = 'chat.id IN(:...ids)';
        } else {
          joinCondition = 'false';
        }
        return this.repository
          .createQueryBuilder('user')
          .where('user.id != :userId', { userId: userId })
          .andWhere(
            new Brackets(qb => {
              qb.where('user.nick_name like :nickName', { nickName: `%${query}%` })
                .orWhere('user.email like :email', { email: `%${query}%` })
                .orWhere('user.phone like :phone', { phone: `%${query}%` })
                .orWhere('user.first_name like :firstName', { firstName: `%${query}%` })
                .orWhere('user.last_name like :lastName', { lastName: `%${query}%` });
            })
          )
          .leftJoinAndSelect('user.avatar', 'avatar')
          .leftJoinAndSelect('user.chats', 'chat', joinCondition, {
            ids: chatsId,
          })
          .leftJoinAndSelect(
            'chat.messages',
            'm',
            'm.id = (SELECT id FROM messages AS m WHERE m.chatId = chat.id ORDER BY m.created_at DESC LIMIT 1)',
          )
          .take(size)
          .skip(skip)
          .getMany();
      }),
    );
  }

  setIsOnline(userId: string, isOnline: boolean) {
    return from(
      this.repository
        .createQueryBuilder('user')
        .update()
        .set({ isOnline: isOnline })
        .execute(),
    );
  }

  update(id: string, updateUserDto: UpdateUserDto) {
    return `This action updates a #${id} user`;
  }

  remove(id: string) {
    return `This action removes a #${id} user`;
  }
}
