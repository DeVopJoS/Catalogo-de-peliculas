package com.listapeliculas.peliculas.services;

import com.listapeliculas.peliculas.dao.IPeliculaRepository;
import com.listapeliculas.peliculas.entities.Pelicula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeliculaService implements IPeliculaService{

    @Autowired
    private IPeliculaRepository repository;

    @Override
    public void save(Pelicula pelicula) {
        repository.save(pelicula);
    }

    @Override
    public Pelicula findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Pelicula> findAll() {
        return (List<Pelicula>) repository.findAll();
    }
}
