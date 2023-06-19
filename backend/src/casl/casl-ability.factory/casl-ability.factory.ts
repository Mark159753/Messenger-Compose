import {
  Ability,
  AbilityBuilder,
  AbilityClass,
  InferSubjects,
} from '@casl/ability';
import { User } from '../../typeorm/entities';
import { CaslAction } from '../casl-action';

type Subjects = InferSubjects<typeof User> | 'all';

export type AppAbility = Ability<[CaslAction, Subjects]>;
export class CaslAbilityFactory {
  createForUser(user: User) {
    const { can, cannot, build } = new AbilityBuilder<
      Ability<[CaslAction, Subjects]>
    >(Ability as AbilityClass<AppAbility>);
  }
}
