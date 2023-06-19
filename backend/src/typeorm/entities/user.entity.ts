import {
  Column,
  Entity,
  JoinColumn, JoinTable,
  ManyToMany,
  OneToOne,
  PrimaryGeneratedColumn
} from "typeorm";
import { BaseEntity } from './base.entity';
import { ChatEntity } from './chat.entity';
import { LocalFileEntity } from './local-file.entity';
import { MessageEntity } from './message.entity';

@Entity('users')
export class User extends BaseEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  first_name: string;

  @Column()
  last_name: string;

  @Column()
  nick_name: string;

  @Column({ nullable: false, unique: true })
  phone: string;

  @Column({ default: false })
  isOnline: boolean;

  @Column({ nullable: false, unique: true })
  email: string;

  @JoinColumn({ name: 'avatarId' })
  @OneToOne(() => LocalFileEntity, { nullable: true })
  avatar?: LocalFileEntity;

  @Column({ nullable: true, select: false })
  public avatarId?: string;

  @Column({ nullable: false, select: false })
  passwordHash: string;

  @Column({ nullable: true, select: false })
  refreshToken: string;

  @ManyToMany(() => ChatEntity, (chatEntity) => chatEntity.users)
  @JoinTable()
  chats: ChatEntity[];

  @ManyToMany(() => MessageEntity, (messageEntity) => messageEntity.author)
  messages: MessageEntity[];
}
