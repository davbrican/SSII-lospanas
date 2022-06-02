const { reverse } = require("dns");

contract Donaciones{
    variable public nombre;

    struct donacion{
        address donante;
        uint cantidad;
    }

    Donacion[] public donacionesArray;
    
    functoin donar() public payable{
        if(msg.value > 0){
            reverse("No se pueden donar valores negativos");
        }
        donacionesArray.forEach(function(donacion){
            Donacion(msg.sender, msg.value);
        }


}