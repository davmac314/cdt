package org.eclipse.cdt.internal.core.model;

/*
 * (c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 */
 
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

public class CModel extends Openable implements ICModel {

	public CModel () {
		this(ResourcesPlugin.getWorkspace().getRoot());
	}

	public CModel(IWorkspaceRoot root) {
		super (null, root, ICElement.C_MODEL);
	}

	public ICProject getCProject(String name) {
		IProject project = getWorkspace().getRoot().getProject(name);			
		return CModelManager.getDefault().create(project);			
	}

	public ICProject[] getCProjects() {
		List list = getChildrenOfType(C_PROJECT);
		ICProject[] array= new ICProject[list.size()];
		list.toArray(array);
		return array;
	}

	/**
	 * Returns the active C project associated with the specified
	 * resource, or <code>null</code> if no C project yet exists
	 * for the resource.
	 *
	 * @exception IllegalArgumentException if the given resource
	 * is not one of an IProject, IFolder, or IFile.
	 */
	public ICProject getCProject(IResource resource) {
		switch(resource.getType()){
			case IResource.FOLDER:
				return new CProject(this, ((IFolder)resource).getProject());
			case IResource.FILE:
				return new CProject(this, ((IFile)resource).getProject());
			case IResource.PROJECT:
				return new CProject(this, (IProject)resource);
			default:
				throw new IllegalArgumentException("element.invalidResourceForProject"); //$NON-NLS-1$
		}
	}

	public IWorkspace getWorkspace() {
		return getUnderlyingResource().getWorkspace();
	}

	public void copy(ICElement[] elements, ICElement[] containers, ICElement[] siblings,
		String[] renamings, boolean replace, IProgressMonitor monitor) throws CModelException {
		if (elements != null && elements[0] != null && elements[0].getElementType() <= ICElement.C_UNIT ) {
			runOperation(new CopyResourceElementsOperation(elements, containers, replace), elements, siblings, renamings, monitor);
		} else {
			throw new CModelException (new CModelStatus());
			//runOperation(new CopyElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
		}
	}

	public void delete(ICElement[] elements, boolean force, IProgressMonitor monitor)
		throws CModelException {
		if (elements != null && elements[0] != null && elements[0].getElementType() <= ICElement.C_UNIT) {
			runOperation(new DeleteResourceElementsOperation(elements, force), monitor);
		} else {
			throw new CModelException (new CModelStatus());
			//runOperation(new DeleteElementsOperation(elements, force), monitor);
		}
	}

	public void move(ICElement[] elements, ICElement[] containers, ICElement[] siblings,
		String[] renamings, boolean replace, IProgressMonitor monitor) throws CModelException {
		if (elements != null && elements[0] != null && elements[0].getElementType() <= ICElement.C_UNIT) {
			runOperation(new MoveResourceElementsOperation(elements, containers, replace), elements, siblings, renamings, monitor);
		} else {
			throw new CModelException (new CModelStatus());
			//runOperation(new MoveElementsOperation(elements, containers, force), elements, siblings, renamings, monitor);
		}
	}

	public void rename(ICElement[] elements, ICElement[] destinations, String[] renamings,
		boolean force, IProgressMonitor monitor) throws CModelException {
		if (elements != null && elements[0] != null && elements[0].getElementType() <= ICElement.C_UNIT) {
			runOperation(new RenameResourceElementsOperation(elements, destinations,
					renamings, force), monitor);
		} else {
			throw new CModelException (new CModelStatus());
			//runOperation(new RenameElementsOperation(elements, containers, renamings, force), monitor);
		}
	}

	/**
	 * Configures and runs the <code>MultiOperation</code>.
	 */
	protected void runOperation(MultiOperation op, ICElement[] elements, ICElement[] siblings, String[] renamings, IProgressMonitor monitor) throws CModelException {
		op.setRenamings(renamings);
		if (siblings != null) {
			for (int i = 0; i < elements.length; i++) {
				op.setInsertBefore(elements[i], siblings[i]);
			}
		}
		runOperation(op, monitor);
	}

	protected CElementInfo createElementInfo () {
		return new CModelInfo(this);
	}

	// CHECKPOINT: Roots will return the hashcode of their resource
	public int hashCode() {
		return resource.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.model.Openable#generateInfos(org.eclipse.cdt.internal.core.model.OpenableInfo, org.eclipse.core.runtime.IProgressMonitor, java.util.Map, org.eclipse.core.resources.IResource)
	 */
	protected boolean generateInfos(OpenableInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws CModelException {
		boolean validInfo = false;
		try {
			IResource res = getResource();
			if (res != null && (res instanceof IWorkspaceRoot || res.getProject().isOpen())) {
				// put the info now, because computing the roots requires it
				CModelManager.getDefault().putInfo(this, info);
				validInfo = computeChildren(info, res);
			}
		} finally {
			if (!validInfo) {
				CModelManager.getDefault().removeInfo(this);
			}
		}
		return validInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.model.ICModel#getNonCResources()
	 */
	public Object[] getNonCResources() throws CModelException {
		return ((CModelInfo)getElementInfo()).getNonCResources();
	}

	protected  boolean computeChildren(OpenableInfo info, IResource res) throws CModelException {
		CModelManager factory = CModelManager.getDefault();

		// determine my children
		IWorkspaceRoot root = (IWorkspaceRoot)getResource();
		IProject[] projects = root.getProjects();
		for (int i = 0, max = projects.length; i < max; i++) {
			IProject project = projects[i];
			if (factory.hasCNature(project) || factory.hasCCNature(project)) {
				ICProject cproject = new CProject(this, project);
				info.addChild(cproject);
			}
		}
		((CModelInfo)getElementInfo()).setNonCResources(null);
		return true;
	}

}
