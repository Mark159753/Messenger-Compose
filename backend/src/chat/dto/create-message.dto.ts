export class CreateMessageDto {
  id: string;
  message: string;
  chatId?: string;
  friendId?: string;
}
