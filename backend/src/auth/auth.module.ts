import { Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from '../typeorm/entities';
import { JwtModule } from '@nestjs/jwt';
import { AccessTokenStrategy } from './srategies/access-token.strategy';
import { RefreshTokenStrategy } from './srategies/refresh-token.stratedy';
import { LocalFileModule } from '../local-file/local-file.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User]),
    JwtModule.register({}),
    LocalFileModule,
  ],
  controllers: [AuthController],
  providers: [AuthService, AccessTokenStrategy, RefreshTokenStrategy],
  exports: [AuthService],
})
export class AuthModule {}
