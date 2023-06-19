import {
  Controller,
  Get,
  Body,
  Patch,
  Delete,
  UseGuards,
  Query,
} from '@nestjs/common';
import { UserService } from './user.service';
import { UpdateUserDto } from './dto/update-user.dto';
import { GetUser } from '../common/user.decorator';
import { User as UserEntity } from '../typeorm/entities';
import { AccessTokenGuard } from '../auth/guards/access-token.guard';

@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @UseGuards(AccessTokenGuard)
  @Get('my')
  findOne(@GetUser() user: UserEntity) {
    return this.userService.findOne(user.id);
  }

  @UseGuards(AccessTokenGuard)
  @Get('search')
  search(
    @GetUser() user: UserEntity,
    @Query('query') query: string,
    @Query() anotherQuery,
  ) {
    return this.userService.search(
      user,
      query,
      anotherQuery.size,
      anotherQuery.page,
    );
  }
  @UseGuards(AccessTokenGuard)
  @Patch()
  update(@GetUser() user: UserEntity, @Body() updateUserDto: UpdateUserDto) {
    return this.userService.update(user.id, updateUserDto);
  }
  @UseGuards(AccessTokenGuard)
  @Delete()
  remove(@GetUser() user: UserEntity) {
    return this.userService.remove(user.id);
  }
}
