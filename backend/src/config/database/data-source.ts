import { DataSource, DataSourceOptions } from 'typeorm';
import * as process from 'process';

//eslint-disable-next-line @typescript-eslint/no-var-requires
require('dotenv').config({ path: 'src/config/env/.env.dev' });

export default new DataSource({
  type: 'postgres',
  host: process.env.TYPEORM_HOST,
  port: Number.parseInt(process.env.TYPEORM_PORT, 10),
  username: process.env.TYPEORM_USERNAME,
  password: process.env.TYPEORM_PASSWORD,
  database: process.env.TYPEORM_DATABASE,
  entities: ['src/typeorm/entities/**/*{.entity.ts,.entity.js}'],
  migrations: ['dist/typeorm/migrations/**/*.js'],
  autoLoadEntities: true,
  synchronize: true,
  logging: ['error'],
  cli: {
    migrationsDir: 'migrations',
  },
} as DataSourceOptions);
