for i in range(100000000):
    val = ""
    if i < 10000000:
        val += "0"+str(i)
    elif i < 1000000:
        val += "00"+str(i)
    elif i < 100000:
        val += "000"+str(i)
    elif i < 10000:
        val += "0000"+str(i)
    elif i < 1000:
        val += "00000"+str(i)
    elif i < 100:
        val += "000000"+str(i)
    elif i < 10:
        val += "0000000"+str(i)
    elif i < 1:
        val += "00000000"+str(i)
    
    val += " "
            
    for j in range(1000000):
        if j < 100000:
            val += "0"+str(i)
        elif j < 10000:
            val += "00"+str(i)
        elif j < 1000:
            val += "000"+str(i)
        elif j < 100:
            val += "0000"+str(i)
        elif j < 10:
            val += "00000"+str(i)
        elif j < 1:
            val += "000000"+str(i)
    
        val += " "
                
        for z in range(1000):
            if z < 100:
                val += "0"+str(i)
            elif z < 10:
                val += "00"+str(i)
            elif z < 1:
                val += "000"+str(i)
        
        print(val)