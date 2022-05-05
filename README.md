# 1. Mini Dynamic Lights

A fast dynamic light mod by doing minimal
computation through:

 - selective dynamic light entity computation by type and count
 - smaller range of dynamic light radius

As a result, the dynamic lighting can appear choppy in expense of performance.
However, dynamic lighting settings (Section 4.) can be tweaked to fit into your system.

# 2. Acknowledgement

Thanks to this source for giving me the best reference 
for me to get started on this mod. 

- [LambDynamicLights - How does it work?](https://github.com/LambdAurora/LambDynamicLights/blob/1.17/HOW_DOES_IT_WORK.md)

# 3. Features

I present to you five basic features to this mini dynamic
lights mod.

## 3.1. Nearest Entities Dynamic Lights

By default, the mod will only render the nearest 4 entities
that are eligible for dynamic lights, which is the lowest
setting for this mod.

[Nearest Entities Rendering] - https://imgur.com/G9ptG41.mp4

If you are interested to increase the settings, head to
the video settings of your option screen.


## 3.2. Animation

When you hold a glowing item, it will glow gradually like this.

[Animation] - https://imgur.com/3jv4cbt.mp4

It's the little things that count. :)

## 3.3. Light Radius

The default setting for light radius is level 3, which is
the medium level. Increase this to get a brighter dynamic
light, and reduce this to get a faster performance.

[Light Radius] - https://imgur.com/zed3Dnx.mp4

## 3.4. Vision-based Lighting

The default player light is a bit in front of the 
player to create an illusion that the glowing item is 
being held in front of the player.

[Vision-based Lighting] - https://imgur.com/ivyQ0Ga.mp4

## 3.5. (Experimental) Entity Configurable

This mod only preconfigured dynamic lights to players
and glow squids, but you can extend the configuration
to your likings!

In your game directory, find the directory `config` and
create a directory named `dynamic-lights`. In
there, create new files in the format `filename.yaml` and the mod
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
are the current ones so far.


# 4. Settings

A lot of the settings here are experimental, you can usually leave
it as it is. However, if you are interested in getting higher
dynamic light resolution, you can refer to the following options in the Video Settings page:


## 4.1 Chunk Builder
Set this option to `Full Blocking` to ensure all lighting animation
to be in-sync. This will usually fix all choppy-ness issues but will also reduce performances.

## 4.2 Smooth Lighting
Set this option to `Minimum` to reduce light smoothness and increase performance.

## 4.3 Dyn. Light Entities
Set this option to determine how many entities to render the dynamic entities.
The lower the number of dynamic light entities, the greater the performance.

Refer to (Section 3.2.) for reference.

## 4.4 Dyn. Light Level
Set this option to control the spreadness of the dynamic lights. The smaller the spread,
 the greater the performance, the more likely it is to be choppy.

## 4.5 DL. Performance
Set this option to `SMOOTH` to always compute for dynamic lights on every tick.
Set this option to `FASTEST` to delay dynamic lights next tick computation.

## 4.5 DL. Precision
Set this option to control to strength of dynamic lights. The greater the strength,
the greater the number of computations, the more likely it is to be choppy.

# 5. Mods Compatibility

This mod is mostly compatible to any mods. Here are a few tested mods with Mini Dynamic Lights:

- [Sodium](https://modrinth.com/mod/sodium): this mod is highly recommended to be added with Mini Dynamic Lights. However, the options page will be overwritten. My future plans include refactor the options page for dynamic lights.