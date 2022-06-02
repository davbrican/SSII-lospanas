// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract Pujas{

    address payable public owner;

    uint256 public precioAnterior;

    bool public subastaActiva;

    struct Puja{
        address payable pujador;
        uint256 cantidad;
    }

    mapping (address => Puja) public pujas;
    Puja[] public pujasArray;

    event Message(string msg);

    constructor(){
        owner = payable(msg.sender);
    }

    function pujar(uint256 cantidad) public payable{
        if (!subastaActiva){ revert("La subasta ha finalizado");}
        else{
            uint256 _cantidad = cantidad/10;
            if(_cantidad <= 0 || _cantidad >= 3500){
                    revert("La puja debe ser mayor a 0 y menor al precio del medicamento (3500)");
            }else{
                if(pujasArray.length == 0){
                    owner.transfer(_cantidad);
                    pujasArray.push(
                        Puja(payable(msg.sender), _cantidad)
                    );
                    precioAnterior = _cantidad;
                    emit Message("Puja realizada");
                }else{
                    if (_cantidad >= pujasArray[0].cantidad){
                        revert("La puja debe ser menor a la ultima puja");
                    } else {
                        owner.transfer(_cantidad);
                        pujasArray.push(
                            Puja(payable(msg.sender), _cantidad)
                        );
                        precioAnterior = pujasArray[0].cantidad;
                        pujasArray[0].pujador.transfer(precioAnterior);
                        delete pujasArray[0];
                        emit Message("Puja realizada");
                    }
                }
            }
        }
    }

    function pagarGanador() public payable{
        if(msg.sender != owner){ revert("Solo el owner puede pagar al ganador"); }
        else if(pujasArray.length == 0){ revert("No hay pujas en la subasta"); }
        else{
            pujasArray[0].pujador.transfer(precioAnterior);
            delete pujasArray;
        }
        emit Message("El dinero ha sido devuelto al ganador");
    }

    function finalizarSubasta() public payable{
        if(msg.sender != owner){ revert("Solo el owner puede finalizar la subasta"); }
        else{
            subastaActiva = false;
        }
        emit Message("La subasta fue finalizada");
    }

}