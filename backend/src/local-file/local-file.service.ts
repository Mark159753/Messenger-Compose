import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { LocalFileEntity } from '../typeorm/entities/local-file.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { LocalFileDto } from './dto/local-file.dto';
import { from, map, Observable, of, switchMap } from 'rxjs';
import { ImageEntity } from '../typeorm/entities/image.entity';

@Injectable()
export class LocalFileService {
  constructor(
    @InjectRepository(LocalFileEntity)
    private readonly localFileRepository: Repository<LocalFileEntity>,
    @InjectRepository(ImageEntity)
    private readonly imagesRepository: Repository<ImageEntity>,
  ) {}

  async saveLocalFile(file?: LocalFileDto) {
    if (!file) return null;
    file.path = file.path.split('\\').join('/');
    const newFile = await this.localFileRepository.create(file);
    await this.localFileRepository.save(newFile);
    return newFile;
  }

  saveListOfImages(files: LocalFileDto[]): Observable<ImageEntity[]> {
    const items = files.map((item) => {
      item.path = item.path.split('\\').join('/');
      return this.imagesRepository.create(item);
    });
    return from(this.imagesRepository.save(items)).pipe(
      map(() => {
        return items;
      }),
    );
  }
}
