import {
  Controller,
  Get,
  Post,
  Body,
  UseGuards,
  UseInterceptors,
  UploadedFile,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { CreateUserDto, SignInDto } from './dto';
import { AccessTokenGuard } from './guards/access-token.guard';
import { RefreshTokenGuard } from './guards/refresh-token.guard';
import { GetUser } from '../common/user.decorator';
import { User as UserEntity } from '../typeorm/entities';
import LocalFilesInterceptor from '../common/interceptors/local-file.interceptor';
import { LocalFileDto } from '../local-file/dto/local-file.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('signIn')
  signUp(@Body() createUserDto: SignInDto) {
    return this.authService.signIn(createUserDto);
  }

  @UseInterceptors(
    LocalFilesInterceptor({
      fieldName: 'avatar',
      path: '/avatars',
    }),
  )
  @Post('signup')
  signupUser(
    @Body() dto: CreateUserDto,
    @UploadedFile() avatar: Express.Multer.File,
  ) {
    let avatarDto: LocalFileDto;
    if (avatar) {
      avatarDto = new LocalFileDto();
      avatarDto.name = avatar.filename;
      avatarDto.originalName = avatar.originalname;
      avatarDto.mineType = avatar.mimetype;
      avatarDto.size = avatar.size;
      avatarDto.path = avatar.path;
    }
    return this.authService.signUp(dto, avatarDto);
  }

  @UseGuards(RefreshTokenGuard)
  @Get('refresh')
  refreshTokens(@GetUser() user: UserEntity) {
    return this.authService.refreshTokens(user.id, user.refreshToken);
  }

  @UseGuards(AccessTokenGuard)
  @Get('logout')
  logoutUser(@GetUser() user: UserEntity) {
    return this.authService.logout(user.id);
  }
}
