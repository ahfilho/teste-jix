package br.com.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import servico.FileStorageService;

public class FilesStorageServiceImpl implements FileStorageService {

	private final Path root = Paths.get("uploads");

	@Override
	public void init() {
		try {
			Files.createDirectories(root);
		} catch (IOException e) {
			throw new RuntimeException("nao foi possível inicializar a pasta para upload");
		}

	}

	@Override
	public void save(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("nao foi possível armazenar o arquivo, desculpe. ERROR" + e.getMessage());

		}
	}

	@Override
	  public Resource load(String filename) {
	    try {
	      Path file = root.resolve(filename);
	      UrlResource resource = new UrlResource(file.toUri());

	      if (resource.exists() || resource.isReadable()) {
	        return (Resource) resource;
	      } else {
	        throw new RuntimeException("Could not read the file!");
	      }
	    } catch (MalformedURLException e) {
	      throw new RuntimeException("Error: " + e.getMessage());
	    }
	  }

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root,1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (Exception e) {
		      throw new RuntimeException("Nao foi possivel carregar os arquivos!");
		}
	}

}
