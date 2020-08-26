# Home Enforcer
*Stop your Mahogany Homes from shifting into other realities.*

When you fix up a home, it's something to admire.
Then you take 8 steps out of the house and it reverts back to the stupid default state.

Jagex decided to have "active regions" where the Mahogany Homes homes are visible, 
and they all share 8 pieces of data to say what should be in what state.

The problem is that they keep the last house you repaired active until you get a new contract.
This means that if your last contract was in an area you walk past a lot, you'll see it shift into
another dimension a lot as it turns into the house you made and back.

This plugin simply saves the state you left the house in, and whenever it tries to set the state back to default,
it will force it to the state you left it in. Since the varbits are shared, this means every house will also share that
fixed state. Think of it as a bonus ;)