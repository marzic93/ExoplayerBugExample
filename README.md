On Android 14, there seems to be an issue with video blinking when switching between videos with varying aspect ratios. 

It looks like the last frame of the first video was applied scale from the second video. The final frame has a black background that causes a blinking effect.

This bug is reproduced on Android 14, everything works fine on previous versions. 


https://github.com/marzic93/ExoplayerBugExample/assets/7560411/7b320a2f-7073-400b-8c07-0de760c2afa2

