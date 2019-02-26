# Player Graves

A simple player gravestones mod for deaths.

How it works? Once you die, a copy of your inventory will be saved<br>
and a gravestone will be created. Once broken, the gravestone will<br>
replace all the items you had back inside your inventory. If another<br>
player attempts to break the gravestone it will be lost and can only<br>
be recovered by server admins.

To recover a grave, simply type

    /recover [player name]
    
There are also a few config options you could edit.
    
    #Allow the player head to render on top of a grave
    #This can be configured only client side
    render-skull: true
    
    #Make a grave act as a skeleton spawner
    grave-spawner: false
