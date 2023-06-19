import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Query,
  UploadedFiles,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { ChatService } from './chat.service';
import { AccessTokenGuard } from '../auth/guards/access-token.guard';
import { User as UserEntity } from '../typeorm/entities';
import { GetUser } from '../common/user.decorator';
import { CrateChatDto } from './dto/crate-chat.dto';
import { CreateMessageDto } from './dto/create-message.dto';
import { LocalFileDto } from '../local-file/dto/local-file.dto';
import MultipleFilesInterceptor from '../common/interceptors/multiple-local-file.interceptor';

@Controller('chat')
export class ChatController {
  constructor(private readonly chatService: ChatService) {}

  @UseGuards(AccessTokenGuard)
  @Post()
  createChat(@GetUser() user: UserEntity, @Body() dto: CrateChatDto) {
    return this.chatService.createChat(user.id, dto.friendUserId);
  }

  @UseGuards(AccessTokenGuard)
  @Get()
  getAllChats(@GetUser() user: UserEntity, @Query() query) {
    return this.chatService.getAllChats(user, query.size, query.page);
  }

  @UseGuards(AccessTokenGuard)
  @Delete(':id')
  removeChat(@Param('id') id: string) {
    return this.chatService.removeChat(id);
  }

  @UseGuards(AccessTokenGuard)
  @UseInterceptors(
    MultipleFilesInterceptor({
      uploadFields: [{ name: 'images', maxCount: 6 }],
      path: '/images',
    }),
  )
  @Post('message')
  createMessage(
    @GetUser() user: UserEntity,
    @Body() dto: CreateMessageDto,
    @UploadedFiles() files: { images: Express.Multer.File[] },
  ) {
    let fileList: LocalFileDto[] = [];
    if (files.images) {
      fileList = files.images.map((item: Express.Multer.File) => {
        const fileDto = new LocalFileDto();
        fileDto.name = item.filename;
        fileDto.originalName = item.originalname;
        fileDto.mineType = item.mimetype;
        fileDto.size = item.size;
        fileDto.path = item.path;
        return fileDto;
      });
    }
    return this.chatService.createMessage(user, dto, fileList);
  }

  @UseGuards(AccessTokenGuard)
  @Get('messages/:id')
  getAllMessages(
    @GetUser() user: UserEntity,
    @Param('id') id: string,
    @Query() query,
  ) {
    return this.chatService.getAllMessage(id, query.size, query.page);
  }
}
