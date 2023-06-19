import { AuthService } from '../../auth/auth.service';
import { Logger, UseGuards } from '@nestjs/common';
import {
  ConnectedSocket,
  MessageBody,
  OnGatewayConnection,
  OnGatewayDisconnect,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { ChatServiceHelper } from '../chat.service.helper';
import { ChatEntity, MessageEntity, User } from '../../typeorm/entities';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
})
export class ChatGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer()
  server: Server;

  private readonly logger = new Logger(ChatGateway.name);

  constructor(private readonly serviceHelper: ChatServiceHelper) {}

  async handleConnection(client: any) {
    this.logger.log('HANDLE CONNECTION');
    const userId = client?.user?.id;
    if (!userId) {
      this.handleDisconnect(client);
      return;
    }
    this.logger.log(`USER_ID -> ${client.user.id}`);
    this.server.socketsJoin(client?.user?.id);
  }

  @SubscribeMessage('events')
  handleEvent(@MessageBody() data: string, @ConnectedSocket() client: any) {
    const userId = client?.user?.id;
    if (userId) {
      this.server.to(userId).emit('events', `HELLO -> ${data}`);
    }
    this.logger.log(data);
  }

  async sendNewMessage(msg: MessageEntity) {
    const chat = await this.serviceHelper.getAllUsersFromChat(msg.chatId);
    chat.users.forEach((user: User) => {
      this.server.to(user.id).emit(MESSAGE_ROOM, msg);
    });
  }

  async sendRemoveChat(chat: ChatEntity){
    chat.users.forEach((user) => {
      this.server.to(user.id).emit(REMOVE_CHAT_ROOM, {chatId: chat.id})
    })
  }

  handleDisconnect(client: any) {
    const userId = client?.user?.id;
    if (userId) {
      this.server.socketsLeave(client?.user?.id);
    }
    client.disconnect(true);
    this.logger.log('DISCONNECTED');
  }
}

const MESSAGE_ROOM = 'messages';
const REMOVE_CHAT_ROOM = 'remove_chat';
