package com.listapeliculas.peliculas.controllers;

import com.listapeliculas.peliculas.entities.Actor;
import com.listapeliculas.peliculas.entities.Pelicula;
import com.listapeliculas.peliculas.services.IActorService;
import com.listapeliculas.peliculas.services.IArchivoService;
import com.listapeliculas.peliculas.services.IGeneroService;
import com.listapeliculas.peliculas.services.IPeliculaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PeliculaController {

    private IPeliculaService service;
    private IGeneroService generoService;
    private IActorService actorService;
    private IArchivoService iArchivoService;

    public PeliculaController(IPeliculaService service, IGeneroService generoService, IActorService actorService, IArchivoService iArchivoService){
        this.generoService = generoService;
        this.service = service;
        this.actorService = actorService;
        this.iArchivoService = iArchivoService;
    }

    @GetMapping("/pelicula")
    public String crear(Model model){
        Pelicula pelicula = new Pelicula();

        model.addAttribute("titulo", "Peliculas");
        model.addAttribute("generos", generoService.findAll());
        model.addAttribute("protagonistas", actorService.findAll());
        model.addAttribute("pelicula", pelicula);

        return "pelicula";
    }

    @GetMapping("/pelicula/{id}")
    public String editar(@PathVariable(name="id") Long id, Model model){
        Pelicula pelicula = service.findById(id);
        String ids = "";

        for (Actor actor: pelicula.getProtagonistas()) {
            if("".equals(ids)){
                ids = actor.getId().toString();
            }else{
                ids = ids + "," + actor.getId().toString();
            }
        }

        model.addAttribute("generos", generoService.findAll());
        model.addAttribute("ids", ids);
        model.addAttribute("protagonistas", actorService.findAll());
        model.addAttribute("titulo", "Editar Pelicula");
        model.addAttribute("pelicula", pelicula);

        return "pelicula";
    }

    @GetMapping("/pelicula/{id}/delete")
    public String eliminar(@PathVariable(name="id") Long id, Model model, RedirectAttributes redirectAttr){

        service.delete(id);

        redirectAttr.addAttribute("msj", "La pelicula fue eliminada correatamente");
        redirectAttr.addAttribute("tipoMsj", "success");

        return "redirect:/listado";
    }

    @PostMapping("/pelicula")
    public  String guardar(@Valid Pelicula pelicula, BindingResult br, @ModelAttribute(name="ids") String ids, Model model, @RequestParam("archivo")MultipartFile imagen){

        if(br.hasErrors()){
            model.addAttribute("generos", generoService.findAll());
            model.addAttribute("protagonistas", actorService.findAll());
            return "pelicula";
        }

        if(!imagen.isEmpty()){
            String archivo = pelicula.getNombre() + getExtension(imagen.getOriginalFilename());
            pelicula.setImagen(archivo);
            try {
                iArchivoService.guardar(archivo, imagen.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            pelicula.setImagen("default.jpg");
        }

        if(ids != null && !"".equals(ids)){
            List<Long> idsProtagonistas = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<Actor> protagonistas = actorService.findAllById(idsProtagonistas);

            pelicula.setProtagonistas(protagonistas);
        }

        service.save(pelicula);
        return "redirect:home";
    }

    private String getExtension(String archivo){
        return archivo.substring(archivo.lastIndexOf("."));
    }

    @GetMapping({"/", "/home", "/index"})
    public String home(Model model){
        model.addAttribute("peliculas", service.findAll());
        //model.addAttribute("msj", "catalogo actualizado");
        //model.addAttribute("tipoMsj", "success");

        return "home";
    }

    @GetMapping("/listado")
    public String listado(Model model, @RequestParam(required = false) String msj, @RequestParam(required = false) String tipoMsj){
        model.addAttribute("titulo", "Listado de peliculas");
        model.addAttribute("peliculas", service.findAll());

        if (!"".equals(tipoMsj) && !"".equals(msj) ){
            model.addAttribute("msj", msj);
            model.addAttribute("tipoMsj", tipoMsj);
        }

        return "listado";
    }
}
