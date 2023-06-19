import { Module } from '@nestjs/common';
import { LocalFileService } from './local-file.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { LocalFileEntity } from '../typeorm/entities/local-file.entity';
import { ImageEntity } from '../typeorm/entities/image.entity';

@Module({
  imports: [TypeOrmModule.forFeature([LocalFileEntity, ImageEntity])],
  providers: [LocalFileService],
  exports: [LocalFileService],
})
export class LocalFileModule {}
