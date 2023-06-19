import { IsNotEmpty } from 'class-validator';

export class CrateChatDto {
  @IsNotEmpty()
  friendUserId: string;
}
