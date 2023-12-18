package com.listapeliculas.peliculas.services;

import com.listapeliculas.peliculas.entities.Genero;

import java.util.List;

public interface IGeneroService {
    public void save(Genero genero);
    public Genero findById(Long id);
    public void delete(Long id);
    public List<Genero> findAll();
}
