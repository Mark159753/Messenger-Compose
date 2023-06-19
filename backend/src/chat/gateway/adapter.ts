import { INestApplicationContext } from '@nestjs/common';
import { Server } from 'socket.io';
import { IoAdapter } from '@nestjs/platform-socket.io';
import { AuthService } from '../../auth/auth.service';

export class AuthenticatedSocketAdapter extends IoAdapter {
  authService: AuthService;

  constructor(private app: INestApplicationContext) {
    super(app);
    this.authService = this.app.get(AuthService);
  }

  createIOServer(port: number, options?: any) {
    const server: Server = super.createIOServer(port, options);

    server.use(async (socket: any, next) => {
      const tokenPayload: string = socket.handshake?.headers?.authorization;

      if (!tokenPayload) {
        return next(new Error('Token not provided'));
      }

      const [method, token] = tokenPayload.split(' ');

      if (method !== 'Bearer') {
        return next(
          new Error('Invalid authentication method. Only Bearer is supported.'),
        );
      }

      try {
        const user = await this.authService.getJwtUser(token);
        if (!user) {
          return next(new Error('Token has expired or is invalid'));
        }
        socket.user = user;
        return next();
      } catch (error: any) {
        return next(new Error('Authentication error'));
      }
    });
    return server;
  }
}
