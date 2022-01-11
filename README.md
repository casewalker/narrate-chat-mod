# Narrate Chat Mod

Minecraft comes with a builtin narrator. For this narrator, there are three narration options available:

* Narrates All
* Narrates Chat
* Narrates System

Regardless of all other thoughts on this matter, there are some people who would like a fourth option. The "_Narrates 
Chat_" option only narrates messages sent publicly through the chat between users. Whispers and system messages are not
narrated. The "_Narrates System_" option narrates a large amount of system messages, including system messages which
pass through the chat interface. A fourth option of narration could be to narrate all chat interface messages, whether
they are public chat messages from users or whispers or system messages.

This mod (when enabled) should behave like that fourth option. Note that this mod is built on top of 
[Fabric](https://fabricmc.net/).

## Configuration

The mod expects to find a configuration file in the `config` directory (either `narratechatmod.yml` or
`narratechatmod.json`) with a single property: `modEnabled`. If no file is found, a `JSON` file will be created
automatically, filled with an empty object, and the mod will be enabled by default.

## License

Licensed under the MIT License (MIT). Copyright © 2021 Case Walker.
