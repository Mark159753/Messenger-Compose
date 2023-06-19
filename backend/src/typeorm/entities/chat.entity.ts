import {
  Entity,
  JoinTable,
  ManyToMany,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';
import { BaseEntity } from './base.entity';
import { MessageEntity } from './message.entity';

@Entity('chats')
export class ChatEntity extends BaseEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @ManyToMany(() => User, (user) => user.chats)
  users: User[];

  @OneToMany(() => MessageEntity, (messageEntity) => messageEntity.chat, { onDelete: 'CASCADE' })
  messages: MessageEntity[];
}
