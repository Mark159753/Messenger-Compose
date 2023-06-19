import { BaseEntity } from './base.entity';
import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryColumn,
  PrimaryGeneratedColumn,
  Unique,
} from 'typeorm';
import { MessageEntity } from './message.entity';

@Entity('images')
export class ImageEntity extends BaseEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ name: 'original_name' })
  originalName: string;

  @Column()
  name: string;

  @Column()
  path: string;

  @Column({ name: 'mine_type' })
  mineType: string;

  @Column()
  size: number;


  @ManyToOne(() => MessageEntity, (message) => message.images , { onDelete: 'CASCADE' })
  message: MessageEntity;
}
