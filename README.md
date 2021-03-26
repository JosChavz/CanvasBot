# CanvasBot

A Discord Bot that reminds users an hour before for upcoming assignments.<br>
*still in the works...*

Only functions for one server, as of now.
## Getting Started
1. `git clone https://github.com/JosChavz/CanvasBot.git`
1. Rename `example_keys.ini` to `keys.ini` in *src/main/java*
1. Input your discord's bot Token next to `discord_keys` in *keys.ini*
   1. [How to get Discord Token](https://www.writebots.com/discord-bot-token/)
1. Input your Canvas API token next to `canvas_keys` in *keys.ini*
    1. [How to get API Keys](https://community.canvaslms.com/t5/Student-Guide/How-do-I-manage-API-access-tokens-as-a-student/ta-p/273)
1. Add your **school** URL looking alike `url`, replacing `[school]` in *keys.ini*, plus add the Course ID replacing `[course ID]`
    1. ex: `url=https://miracosta.instructure.com/api/v1/courses/12345/assignments`
    
##### Prior Logs
https://github.com/discord-canvas-api/CanvasAPI
