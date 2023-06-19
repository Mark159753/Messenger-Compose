import { BadRequestException, ForbiddenException, Injectable } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { CreateUserDto, SignInDto } from "./dto";
import { InjectRepository } from "@nestjs/typeorm";
import { User } from "../typeorm/entities";
import { Repository } from "typeorm";
import * as argon2 from "argon2";
import { JwtService } from "@nestjs/jwt";
import { LocalFileService } from "../local-file/local-file.service";
import { LocalFileDto } from "../local-file/dto/local-file.dto";

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User)
    private readonly repository: Repository<User>,
    private jwtService: JwtService,
    private configService: ConfigService,
    private readonly localFileService: LocalFileService,
  ) {}

  async signUp(
    createUserDto: CreateUserDto,
    avatar?: LocalFileDto,
  ): Promise<any> {
    // Check if user exists
    const userExists = await this.repository.findOneBy({
      email: createUserDto.email,
    });
    if (userExists) {
      throw new BadRequestException('User already exists');
    }

    // Hash password
    const hash = await this.hashData(createUserDto.password);
    const localAvatar = await this.localFileService.saveLocalFile(avatar);
    const newUser = await this.repository.save({
      ...createUserDto,
      avatar: localAvatar,
      passwordHash: hash,
    });
    const tokens = await this.getTokens(newUser.id, newUser.email);
    await this.updateRefreshToken(newUser.id, tokens.refreshToken);
    return tokens;
  }

  async signIn(data: SignInDto): Promise<any> {
    // Check if user exists
    const user = await this.repository.findOne({
      select: ['id', 'email', 'passwordHash'],
      where: { email: data.email },
    });
    if (!user) throw new BadRequestException('User does not exist');
    const passwordMatches = await argon2.verify(
      user.passwordHash,
      data.password,
    );
    if (!passwordMatches)
      throw new BadRequestException('Password is incorrect');
    const tokens = await this.getTokens(user.id, user.email);
    await this.updateRefreshToken(user.id, tokens.refreshToken);
    return tokens;
  }

  async refreshTokens(userId: string, refreshToken: string) {
    const user = await this.repository.findOne({
      select: ['id', 'email', 'refreshToken'],
      where: { id: userId },
    });
    if (!user || !user.refreshToken)
      throw new ForbiddenException('Access Denied');
    const refreshTokenMatches = await argon2.verify(
      user.refreshToken,
      refreshToken,
    );
    if (!refreshTokenMatches) throw new ForbiddenException('Access Denied');
    const tokens = await this.getTokens(user.id, user.email);
    await this.updateRefreshToken(user.id, tokens.refreshToken);
    return tokens;
  }

  async logout(userId: string) {
    return this.repository.update({ id: userId }, { refreshToken: null });
  }

  hashData(data: string) {
    return argon2.hash(data);
  }

  async getTokens(userId: string, email: string) {
    const [accessToken, refreshToken] = await Promise.all([
      this.jwtService.signAsync(
        {
          id: userId,
          email,
        },
        {
          secret: this.configService.get<string>('JWT_ACCESS_SECRET'),
          expiresIn: '60m',
        },
      ),
      this.jwtService.signAsync(
        {
          id: userId,
          email,
        },
        {
          secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
          expiresIn: '7d',
        },
      ),
    ]);
    return {
      accessToken,
      refreshToken,
    };
  }

  async updateRefreshToken(userId: string, refreshToken: string) {
    const hashedRefreshToken = await this.hashData(refreshToken);
    await this.repository.update(userId, {
      refreshToken: hashedRefreshToken,
    });
  }

  async getJwtUser(jwt: string): Promise<User | null> {
    return await this.jwtService
      .verifyAsync(jwt, {
        secret: this.configService.get<string>('JWT_ACCESS_SECRET'),
      })
      .catch((e: any) => null);
  }
}
