import {
  Column,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { ChatEntity } from './chat.entity';
import { BaseEntity } from './base.entity';
import { User } from './user.entity';
import { ImageEntity } from './image.entity';

@Entity('messages')
export class MessageEntity extends BaseEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ nullable: true })
  message?: string;

  @Column()
  chatId: string;

  @ManyToOne(() => ChatEntity, (chatEntity) => chatEntity.messages, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'chatId' })
  chat: ChatEntity;

  @ManyToOne(() => User, (user) => user.id)
  author: User;

  @OneToMany(() => ImageEntity, (file) => file.message)
  images: ImageEntity[];
}
