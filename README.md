# So Long As This Then That (SLATTT) Home Automation

Reimagining home automation control as Denotative Continuos-Time Programming (DCTP). The traditional approach is built on the idea of "if X happens then do Y". This is an approach based on event triggers or signal edges. Instead, SLATTT transforms all the possible inputs (i.e. switches and current time) into the expected state of all controllable things. An approach that can be summarized as "so long as X is the case then the state should be Y else Z". That means whenever X becomes true and as long as it is true, then it will ensure Y. Or alternatively, the state should always be `f(x)`.

## Advantages

* Recovers from missed events and service outages
* Always correctly handles things that change continuously over time
* Simpler to think about because there is no state within the home automation control program

## An Example

Consider an attempt to configure a routine for controlling lights. I want lights to be on during the day if I'm home and at 9 pm I want an hour long sunset simulation to run (using colored lights). In my [Hubitat Elevation](https://hubitat.com/) home automation system, this could be done with rules equivalent to:

1. If cell phone becomes away then set mode to "away"
2. If cell phone becomes present then set mode based on time of day:
   * When time is 6:30 am to 10 pm then mode is "day"
   * When time is 10 pm to 6:30 am then mode is "night"
3. If mode becomes "day" then turn on lights
4. If mode becomes "night" or "away" then turn off lights
5. At 9:00 pm, if mode is "day" then start sunset sequence

So what happens if I'm out later than expected and return home at 9:01 pm? Well, mode becomes "day", but the sunset sequence isn't run because the time to start it has already past. That's not what I wanted. In an attempt to fix that, I changed to the rules to:

1. If cell phone becomes away then set mode to "away"
2. If cell phone becomes present then set mode based on time of day:
   * If time is 6:30 am to 9 pm then mode is "day"
   * If time is 9 pm to 10 pm then mode is "bedtime"
   * If time is 10 pm to 6:30 am then mode is "night"
3. If mode becomes "day" or "bedtime" turn on lights
4. If mode becomes "night" or "away" then turn off lights
5. If mode becomes "bedtime" then start sunset sequence

What happens with these new rules when I return home at 9:30 pm? Well, the mode becomes "bedtime", and that starts the sunset sequence. That's not what I wanted. The sunset sequence should be half over, but instead it is just starting and will only be half over when the lights turn out at 10 pm.

That example shows the challenges of trying to properly configure current home automation systems. Of course, I didn't even discuss how badly the system handled the sunset sequence itself. So is there a better way? Well, there is actually a clue in the rules. Notice the rule for setting the mode when returning home isn't based on time triggers. Instead, the mode is a *function* of the time. That is, the system takes the current time and computes the mode from it. This is a special feature of the [Hubitat Elevation](https://hubitat.com/) for the modes app only. What if we apply that idea to the whole system? So the desired state of the lights is a function of both the time of day and whether the phone is present. We don't have to worry about whether the triggers fire. The system will constantly monitor the time of day, state of the cell phone, and the lights and make sure the lights are set to the desired state. One way to describe a function is with a table. So the function for my desired lighting is:

 | Time            | Present    | Away |
 | --------------- | ---------- | ---- |
 | 6:30 am - 9 pm  | On (white) | Off  |
 | 9 pm - 10 pm    | Sunset     | Off  |
 | 10 pm - 6:30 am | Off        | Off  |

This approach also makes it easy to ensure the sunset program always runs as if started at 9 pm. When I return home, the function will compute the desired color of the lights at that time and set the lights to that.
