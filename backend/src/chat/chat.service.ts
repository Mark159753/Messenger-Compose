import { Injectable } from '@nestjs/common';
import { ChatEntity, MessageEntity, User } from '../typeorm/entities';
import { Observable, tap } from 'rxjs';
import { CreateMessageDto } from './dto/create-message.dto';
import { ChatServiceHelper } from './chat.service.helper';
import { ChatGateway } from './gateway/chat.gateway';
import { LocalFileDto } from '../local-file/dto/local-file.dto';

@Injectable()
export class ChatService {
  constructor(
    private readonly serviceHelper: ChatServiceHelper,
    private readonly gateway: ChatGateway,
  ) {}

  createChat(
    creatorUserId: string,
    friendUserId: string,
  ): Observable<ChatEntity> {
    return this.serviceHelper.createChat(creatorUserId, friendUserId);
  }

  getAllChats(user: User, size = 10, page = 1) {
    return this.serviceHelper.getAllChats(user, size, page);
  }

  removeChat(chatId: string) {
    return this.serviceHelper.removeChat(chatId).pipe(
      tap((chat: ChatEntity) => {
        this.gateway.sendRemoveChat(chat)
      }),
    );
  }

  createMessage(user: User, dto: CreateMessageDto, images: LocalFileDto[]) {
    return this.serviceHelper.createMessage(user, dto, images).pipe(
      tap((msg: MessageEntity) => {
        this.gateway.sendNewMessage(msg);
      }),
    );
  }

  getMessageById(messageId: string) {
    return this.serviceHelper.getMessageById(messageId);
  }

  getAllMessage(chatId: string, size = 10, page = 1) {
    return this.serviceHelper.getAllMessage(chatId, size, page);
  }
}
