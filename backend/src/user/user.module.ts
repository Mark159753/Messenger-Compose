import { Module } from '@nestjs/common';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ChatEntity, User } from '../typeorm/entities';

@Module({
  imports: [TypeOrmModule.forFeature([User, ChatEntity])],
  controllers: [UserController],
  providers: [UserService],
})
export class UserModule {}
