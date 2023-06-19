import { Injectable, mixin, NestInterceptor, Type } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import {
  MulterField,
  MulterOptions,
} from '@nestjs/platform-express/multer/interfaces/multer-options.interface';
import { diskStorage } from 'multer';
import e from 'express';
import { FileFieldsInterceptor } from '@nestjs/platform-express';

interface MultipleFilesInterceptorOptions {
  uploadFields: MulterField[];
  path?: string;
}

function MultipleFilesInterceptor(
  options: MultipleFilesInterceptorOptions,
): Type<NestInterceptor> {
  @Injectable()
  class Interceptor implements NestInterceptor {
    fileInterceptor: NestInterceptor;
    constructor(configService: ConfigService) {
      const filesDestination = configService.get('UPLOADED_FILES_DESTINATION');

      const destination = `${filesDestination}${options.path}`;

      const multerOptions: MulterOptions = {
        storage: diskStorage({
          destination: destination,
          filename(
            req: e.Request,
            file: Express.Multer.File,
            callback: (error: Error | null, filename: string) => void,
          ) {
            const uniqueSuffix = `${Date.now()}-${Math.round(
              Math.random() * 1e9,
            )}`;
            const originalNameWithoutExtension = file.originalname
              .split('.')
              .slice(0, -1)
              .join('.');
            const filename = `${originalNameWithoutExtension}-${uniqueSuffix}.${getExt(
              file.originalname,
            )}`;
            callback(null, filename);
          },
        }),
      };

      this.fileInterceptor = new (FileFieldsInterceptor(
        options.uploadFields,
        multerOptions,
      ))();
    }

    intercept(...args: Parameters<NestInterceptor['intercept']>) {
      return this.fileInterceptor.intercept(...args);
    }
  }
  return mixin(Interceptor);
}

function getExt(filename: string): string {
  const extension = filename.split('.').pop();
  return extension.toLowerCase();
}

export default MultipleFilesInterceptor;
