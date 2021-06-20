# Mini Dynamic Lights

A fast dynamic light mod by doing minimal
computation through:

 - selective dynamic light entity computation by type and count
 - smaller range of dynamic light radius

# Acknowledgement

Thanks to this source for giving me the best reference 
for me to get started on this mod. 

- [LambDynamicLights - How does it work?](https://github.com/LambdAurora/LambDynamicLights/blob/1.17/HOW_DOES_IT_WORK.md)

# Features

I present to you five basic features to this mini dynamic
lights mod.

## 1. Nearest Entities Dynamic Lights

By default, the mod will only render the nearest 4 entities
that are eligible for dynamic lights, which is the lowest
setting for this mod.

![Nearest Entities Rendering](https://imgur.com/G9ptG41.gif)

[Play GIF](https://imgur.com/G9ptG41.gif)

If you are interested to increase the settings, head to
the video settings of your option screen.


## 2. Animation

When you hold a glowing item, it will glow gradually like this.

![Animation](https://imgur.com/3jv4cbt.gif)

[Play GIF](https://imgur.com/3jv4cbt.gif)

It's the little things that count. :)

## 3. Light Radius

The default setting for light radius is level 3, which is
the medium level. Increase this to get a brighter dynamic
light, and reduce this to get a faster performance.

![Light Radius](https://imgur.com/zed3Dnx.gif)

[Play GIF](https://imgur.com/zed3Dnx.gif)

## 4. Vision-based Lighting

The default player light is a bit in front of the 
player to create an illusion that the glowing item is 
being held in front of the player.

![Vision-based Lighting](https://imgur.com/ivyQ0Ga.gif)

[Play GIF](https://imgur.com/ivyQ0Ga.gif)

## 5. Entity Configurable

This mod only preconfigured dynamic lights to players
and glow squids, but you can extend the configuration
to your likings!

In your game directory, find the directory `config` and
create a directory named `dynamic-lights`. In
here, drop all your configuration files, and the mod
will iterate over each file to apply your new configuration.

This is a configuration template for entities
```yaml
# a compulsory field
type: entity

# minecraft id for the entity
id: minecraft:player

# light emitted will be based on 
# held items, has a stronger precedence
# over the next field.
# if left empty, the default value
# is false
light strength by item: false

# light emitted based on a static value
# if left empty, the default value
# is 0
light source offset: 0
```

To define lights for items, you can use this configuration
```yaml
# a compulsory field
type: item

# minecraft id for the item
id: minecraft:torch

# light emitted based on a static value
# if left empty, the default value
# is 0
light source offset: 0
```

There will be more configurations in the future, but these
are the final one so far.
