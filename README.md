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
 ___
 ## Other Commands
 
 ### Avatar Commands
 ___
 Usage: `!avatar <user tag>`
 
 - Shows the user's avatar
 
 Who can use it? 
 
 - People that can ban users from the server.
 
___
  
 Usage: `!setavatar <image url>`
 
 - Sets the avatar of the bot to the url
 ***MUST BE A URL!***
 
 Who can use it?
 
 - People who can ban users from the server
 
 ### Cake Command
 ___
 Usage: `!cake [optional ingredients]`
 
 - Bakes you a cake!
 
 Who can use it?
 
 - Anyone, in #off-topic or #patreons
 
 ### Embed Command
 ___
 Usage: `!embed <embed url>`
 
 - Preferably a gist, or some other unexpiring JSON link. The data will be gotten, 
 made into an embed, and sent by the bot.
 
 Who can use it?
 
 - Anyone, in #off-topic or anywhere for people who can kick users from the server.
 
 ### GitHub Command
 ___
 Usage: `!github <username/repo> [issue #]`
 
 - Shows information about a repository, in the form of an embed. If an issue number is provided
  then information about the specific issue will be provided. There are built-in shortcuts to:
  
  > - the LP repo (lp, lperms, luckperms)
  > - the LPWeb repo (lpw, lpweb, luckpermsweb)
  > - the VCF repo (vcf, vaultchatformatter)
  > - the ExtraContexts repo (ec, extracontexts)
  > - the api-cookbook (cookbook)
  > - clippy (clippy)
  
 Who can use it? 
 
 - Anyone, anywhere. Useful for displaying issue information by number, and calling up solutions.
 
 - `.luck` or `.lucko` will send an embed with something for luck to be proud of :)
 
 ### Inspiration Command
 ___ 
 Usage: `!inspireme`
 
 - Will send a link from the inspirobot API, which generates random 'inspirational' sayings and 
 sends them in the form of a .jpg link.
 
 Who can use it?
 
 -Anyone, in #off-topic, the patreon, or other non-support/LP conversational channels.
 
 ### Mojang Command
 ___
 Usage: `!mojang`
 
 - Shows the status of mojang authentication servers from `http://status.mojang.com/check`
 
 Who can use it?
 
 - Anyone, anywhere. Useful for checking if a failed mojang authentication error is caused by 
 something else when users send a snapshot of the error.
 
 ### Nickname Command
 ___
 Usage: `!setavatar <image url>`
 
 - Sets the bot's nickname
 
 Who can use it?
 
 - Those that have the `staff` role.
 
 ### Presence Command
 ___
 Usage: `!presence <WATCHING|PLAYING|STREAMING> <Thing doing> <streaming url>`
 
 - Sets the bot's activity. If streaming, a `twitch.tv/accountname` or equivalent URL is required to 
 direct to.
 
 Who can use it?
 
 - Anyone that is has staff role.
 
 ### RoleReaction Command
 ___
 Usage `!rolepoll`
 
 - Sends an embed with a message telling users to click the reaction to subscribe. If they click, a 
 role is given to them which has no extra permissions and can be pinged with updates.
 
 Who can use this?
 
 - Anyone with permission to ban users from the server can use this command.
 
 ### Say Command
 ___
 Usage: `!say [channel|user] <words>`
 
 - Sends the words you specify to the channel you are in unless you mention a channel to say them in or a user to DM them to.
 
 Who can use this? 
 
 - Anyone that can ban users from the server.
 
 ### SecretCommands Command
 ___
 Usage: `!secretcommands`
 
 - Lists all registered commands and their descriptions/usages, in a paginated embed.
 
 Who can use it? 
 
 - Anyone that has permission to ban users from the server.
 
 ### Spiget Command
 ___
 Usage: `!spiget <query>`
 
 - Searches SpigotMC resources and lists the top five, with links and ratings to the hundredth decimal 
 place.
 
 Who can use it?
 
 - Anyone, in #off-topic
 
 ### Tag Command
 Usage: 
 
 - `!tagset <name> [marker]<contents>` sets a tag. [marker] is optional and can be `<embed>` 
 or `<json>` and mark the following url (no spaces) to be made into an embed if json formatted
 embed text is at the address. With no marker, the tag when called will just send the contents 
 as a chat message.
 - `tagunset <name>` unsets a tag
 - `!tagname` calls upon the tag
 - `!tagfile` gets the tagfile
 - `!tags` lists all tags
 - `!tagraw` gets a tag's raw content. If this is a url to embed, will send the url. 
 
 Who can use it?
 
 - Those with permission to kick users can set, unset, tagraw and list. Those that have permission
 to ban users can get the tagfile.
 
 ### UserTag Command
 ___
 Usage: `!utags`, `!utagset`, `!utagunset`, `!utaginfo`, and `!utw`. Very similar usage to those listed above.
 `!utw` will add a user to the usertag whitelist, which allows individuals to use the utag commands.
 
 Who can use it?
 
 - Users who can see the patreons channel and have been added to the whitelist can add user tags. 
 Anyone can use them once they are added. Users that own a utag or can kick people from the server
 can remove utags.
 
 ### WolframAlpha Command
 ___
 Usage: `!wolfram <query>`
 
 - Searches WolframAlpha for the query and returns the result
 
 Who can use it?
 
 - This command can only be used in the patreons, staff, and helpful channels, due in part to it being a fun bonus, 
 and also the hard 2000 request/month limit which could be reached if a bunch of people used it 
 a bunch.
 
 ### Xkcd Command
 ___
 Usage: `!xkcd <query>`
 
 - Searches xkcd for the query.
 
 Who can use it?
 
 - This command can only be used in the staff, helpful channels and the patreons channel, as a fun added bonus!
 ___
 ___
 ___
 
 ## Listeners
 ### AutoMod Listener
 ___
 Listens for a variety of things, acting upon/logging where appropriate.
 - `!pingok` command will trigger the antiping to disable for the channel any mod runs it in, for that mod.
 Users that can kick users or are listed as having a protected role (staff/helpful) are able to run this command.
 
 - `!fileblacklist <arg>` command will add/remove a filetype to the blacklist, which will be deleted if sent. 
 Users that can ban people from the server can add/remove blacklisted extensions.
 
 - `!addcensor <word/regex>` will add a censored word, which will be deleted and logged if detected.
 
 Listeners are active for:
 - Censoring
 - File blacklist removal
 - Antiping (Users will be warned several times and then kicked. There is a cooldown so 
 it does not last forever)
 
 ### AutoUpload Listener
 ___
 This listener will get text files and upload them to bytebin
 
 ### Error Listener
 ___
 This listener will get pastebin service links, scan them, and report on any known LP errors faster than 
 any human can with a description and url to relevant wiki section.
 
 ### ModLog Listeners
 ___
 This listener will log just about everything:
 - Message delete
 - Message edit
 - Bans
 - Kicks
 - New members
 - Leavers
 - Name Changes
 - Nickname Changes
 - Role additions
 - Role removals
 
 ### Private Listener
 ___
 This listener will get any message beginning with the keyword `topsecret` and send it to the PRIVATE_CHANNEL 
 defined in the Constants class. It will also send DMs to the bot to that channel. 
 
 ### Wiki Listener
 ___
 This listener is responsible for all the previous wiki/help commands functioning as normal.
 
 The `!help` command will show all wiki-labelled commands in an identically-formatted embed.
 
 The available commands will not change, but not all will be listed in `!help` in order to shorten the list. 
 
 Commands like `!nwc` will still function but will not be visible. The `!help` command information will take
 the URL defined in the listener and scan it for the latest wiki commands. 
 
 If the URL cannot be accessed,
 a backup which is baked into the JAR will be used instead. 
 
 ### Update Listener
 ___
 This listener will gently remind people that Luckperms already works on 1.16 if they 
 send a message that contains the keywords "1.16" and "update", among others.
 
 ### Log Listener, Util, Storage Combo
 ___
 This listener prints the creation of every new message (except those in the logs channel) to a log file, as well as everything Clippy logs. It 
 uses a custom format for fitting as much information in as little space as possible. Each log file will fill up
 to approximately 1GB before the bot automatically rotates to a new one, and up to 100 log files will be created.
 After 100 GB of pure text logs accumulate, it's definitely time to zip them up and move them out, to start fresh. 
 
 ___
 ___
 ___
 *Notice: a MANIFEST.MF is included if maven is not something you want to use to build the JAR.* 