package com.cefn.filesystem.traversal;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.cefn.filesystem.File;
import com.cefn.filesystem.Filesystem;
import com.cefn.filesystem.Folder;
import com.cefn.filesystem.Visitable;
import com.cefn.filesystem.Visitor;
import com.cefn.filesystem.factory.FileFactory;
import com.cefn.filesystem.factory.FolderFactory;

public class CachedTraversal extends AbstractTraversal{
		
	private final transient EntityManager entityManager;
	
	@Inject
	CachedTraversal(FolderFactory folderFactory, FileFactory fileFactory, EntityManager entityManager) {
		super(folderFactory, fileFactory);
		this.entityManager = entityManager;
	}

	@Override
	public Visitable<Folder> getFolderVisitable(final Filesystem fs) {
		return new Visitable<Folder>(){
			@Override
			public void accept(Visitor<Folder> visitor) {
				Query query = entityManager.createQuery("SELECT folder FROM Folder folder WHERE folder.filesystem = :filesystem");
				query.setParameter("filesystem", fs);
				Iterator<Folder> folderIterator = ((List<Folder>)query.getResultList()).iterator();
				while(folderIterator.hasNext()){
					visitor.visit(folderIterator.next());
				}
			}
		};
	}

	@Override
	public Visitable<File> getFileVisitable(final Folder fs) {
		return new Visitable<File>(){
			@Override
			public void accept(Visitor<File> visitor) {
				Query query = entityManager.createQuery("SELECT folder FROM File folder WHERE folder.filesystem = :filesystem");
				query.setParameter("folder", fs);
				Iterator<File> fileIterator = ((List<File>)query.getResultList()).iterator();
				while(fileIterator.hasNext()){
					visitor.visit(fileIterator.next());
				}
			}
		};
	}

	
}
