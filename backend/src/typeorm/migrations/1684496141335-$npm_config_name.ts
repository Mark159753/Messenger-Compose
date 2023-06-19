import { MigrationInterface, QueryRunner } from "typeorm";

export class  $npmConfigName1684496141335 implements MigrationInterface {
    name = ' $npmConfigName1684496141335'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`CREATE TABLE "messages" ("created_at" TIMESTAMP NOT NULL DEFAULT now(), "updated_at" TIMESTAMP NOT NULL DEFAULT now(), "id" uuid NOT NULL DEFAULT uuid_generate_v4(), "message" character varying NOT NULL, "chatId" uuid, CONSTRAINT "PK_18325f38ae6de43878487eff986" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "chats" ("created_at" TIMESTAMP NOT NULL DEFAULT now(), "updated_at" TIMESTAMP NOT NULL DEFAULT now(), "id" uuid NOT NULL DEFAULT uuid_generate_v4(), CONSTRAINT "PK_0117647b3c4a4e5ff198aeb6206" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE TABLE "chats_users_users" ("chatsId" uuid NOT NULL, "usersId" uuid NOT NULL, CONSTRAINT "PK_8227865724042418a8c1ceada56" PRIMARY KEY ("chatsId", "usersId"))`);
        await queryRunner.query(`CREATE INDEX "IDX_3d891de1ee6dc86bb9d6c9f044" ON "chats_users_users" ("chatsId") `);
        await queryRunner.query(`CREATE INDEX "IDX_91c62ffedcb3d34053b698b56e" ON "chats_users_users" ("usersId") `);
        await queryRunner.query(`ALTER TABLE "messages" ADD CONSTRAINT "FK_36bc604c820bb9adc4c75cd4115" FOREIGN KEY ("chatId") REFERENCES "chats"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "chats_users_users" ADD CONSTRAINT "FK_3d891de1ee6dc86bb9d6c9f044e" FOREIGN KEY ("chatsId") REFERENCES "chats"("id") ON DELETE CASCADE ON UPDATE CASCADE`);
        await queryRunner.query(`ALTER TABLE "chats_users_users" ADD CONSTRAINT "FK_91c62ffedcb3d34053b698b56e0" FOREIGN KEY ("usersId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "chats_users_users" DROP CONSTRAINT "FK_91c62ffedcb3d34053b698b56e0"`);
        await queryRunner.query(`ALTER TABLE "chats_users_users" DROP CONSTRAINT "FK_3d891de1ee6dc86bb9d6c9f044e"`);
        await queryRunner.query(`ALTER TABLE "messages" DROP CONSTRAINT "FK_36bc604c820bb9adc4c75cd4115"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_91c62ffedcb3d34053b698b56e"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_3d891de1ee6dc86bb9d6c9f044"`);
        await queryRunner.query(`DROP TABLE "chats_users_users"`);
        await queryRunner.query(`DROP TABLE "chats"`);
        await queryRunner.query(`DROP TABLE "messages"`);
    }

}
