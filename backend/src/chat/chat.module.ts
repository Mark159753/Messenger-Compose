import { Module } from '@nestjs/common';
import { ChatController } from './chat.controller';
import { ChatService } from './chat.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ChatEntity, MessageEntity, User } from '../typeorm/entities';
import { ChatGateway } from './gateway/chat.gateway';
import { AuthModule } from '../auth/auth.module';
import { ChatServiceHelper } from './chat.service.helper';
import { LocalFileModule } from '../local-file/local-file.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([ChatEntity, User, MessageEntity]),
    AuthModule,
    LocalFileModule,
  ],
  controllers: [ChatController],
  providers: [ChatService, ChatGateway, ChatServiceHelper],
  exports: [ChatServiceHelper],
})
export class ChatModule {}
