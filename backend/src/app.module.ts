import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PostgresConfigService } from './config/database/typeorm-options';
import { AuthModule } from './auth/auth.module';
import { UserModule } from './user/user.module';
import { CaslModule } from './casl/casl.module';
import { ChatModule } from './chat/chat.module';
import { LocalFileModule } from './local-file/local-file.module';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['./src/config/env/.env.dev'],
    }),
    TypeOrmModule.forRootAsync({
      useClass: PostgresConfigService,
      inject: [PostgresConfigService],
    }),
    ServeStaticModule.forRoot({
      rootPath: join(__dirname, '..', 'static/'),
      serveRoot: '/static',
    }),
    AuthModule,
    UserModule,
    CaslModule,
    ChatModule,
    LocalFileModule,
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
