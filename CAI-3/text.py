from numpy import byte
val = str.encode(str('0'*16))

val2 = str(int(val.decode('utf-8')) + 1)

while (len(val2) < 16):
    val2 = '0' + val2
    
val2 = str.encode(val2)

print(val2)