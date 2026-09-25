package com.example.beta_1_Bizti.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beta_1_Bizti.models.Reto;
import com.example.beta_1_Bizti.repository.IReporsitorioReto;

@Service 
public class ServiciosReto {

    @Autowired 
    private IReporsitorioReto reporsitorioReto;

    //Guardar
    public Reto guardaReto(Reto datoReto){
        return this.reporsitorioReto.save(datoReto);
    }
    
}
