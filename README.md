# Clippy the Second

The dream modbot, assistant, and cake chef for any discord.

## Bot

This bot will hopefully one day grow big and strong enough to let good ol' clippy retire and take his place.

### What is this in? 

It be Javacord

### What can Clippy II do that Clippy I could not?

* personal tags for displaying information, full moderatable
* serverwide tags for displaying luckperms info
* fully editable command list that doesn't show everything (thank goodness)
* role on join
* full user mod
* modlogs
* bonus commands for patreons
* some fun commands to ventriloquise with
* embed support to make and display useful embeds both as tags, usertags, and commands
* WolframAplha API connection, allowing for cool questions to get answered
* bot status control
* make cake!! yes finally!
* bot avatar control
* much, much more

## Run-thorugh of Features and Functions

The main class contains a sorted list of listeners and which users can use them. 
To modify who can use things, simply change the conditions for the command in the 
class that's registered.

The following is a breakdown of each class and its function, sorted in the order 
of listing of the directories and files, alphabetically. 

## Moderation Commands
### Ban Command
___
Usage: `!ban <user tag> <reason>`. 

- If the command has all three components, the 
user will be banned. 24 hours of their messages will be deleted.

Who can use it?

- If the person running the command has server permissions to ban users, the bot will comply. 
If not, the bot will simply ignore them. A person with staff will not be banned by the bot.

### Kick Command
___
Usage: `!kick <user tag> <reason>`

- If the command has all three components, the mentioned user will be kicked.

Who can use it?

- If the person running the bot has permission to kick users, they will be able to use the bot to do the same.
A person that is staff will not be kicked by the bot.

### Mute Command
___
Usage: `!mute <user tag> <reason>`

- If the command has all three components, the mentioned user will be muted. If a user is not mentioned, 
the bot will prompt for a name. If the mentioned user is staff, they will not be muted. If the author cannot
 meet the stated requirements, the bot will say no.
 
 Who can use it?
 
 - Anyone with permission to mute members on the server.
 
 ### Prune Command
 ___
 Usage: `!prune <amount>`
 
 - Prunes a certain amount of messages, between 2 and 100. Very useful for megaspammers! If the 
 command author can kick users from the server, and provides a valid number to prune, the command will work.
 
 Who can use it?
 
 - Anyone that can kick users from the server is able to prune.
 
 ### 
 