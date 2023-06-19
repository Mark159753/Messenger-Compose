import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';

@Entity('local_file')
export class LocalFileEntity {
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
}
