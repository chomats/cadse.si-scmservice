package fr.imag.adele.cadse.si.scm;

import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.manager.NoSuchScmProviderException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.starteam.StarteamScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.synergy.SynergyScmProvider;
import org.apache.maven.scm.provider.vss.VssScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.scm.repository.UnknownRepositoryStructure;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import fr.imag.adele.cadse.as.platformide.IPlatformIDE;
import fr.imag.adele.cadse.as.scm.SCMService;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.content.ContentItem;

public class SCMServiceImpl implements SCMService {

	IPlatformIDE			_platformIDE;

	CadseDomain				_cadseDomain;
	
	private static final String TYPE_CVS = "cvs";
	private static final String TYPE_SVN = "svn";
	private static final String TYPE_PERFORCE = "perforce";
	private static final String TYPE_STARTEAM = "starteam";
	private static final String TYPE_CLEARCASE = "clearcase";
	private static final String TYPE_SYNERGY = "synergy";
	private static final String TYPE_VSS = "vss";
	private static final String TYPE_HG = "hg";
	private static final String TYPE_GIT = "git";
	private static final String TYPE_BAZAAR = "bazaar";
	private static final String TYPE_ACCUREV = "accurev";

	private BasicScmManager scmManager;
	
	public SCMServiceImpl() {
	    scmManager = new BasicScmManager();
	    
	    scmManager.setScmProvider(TYPE_CVS, new CvsJavaScmProvider());
	    // scmManager.setScmProvider("cvs", new CvsExeScmProvider());
	    scmManager.setScmProvider(TYPE_SVN, new SvnExeScmProvider());

	    scmManager.setScmProvider(TYPE_PERFORCE, new PerforceScmProvider());
	    scmManager.setScmProvider(TYPE_STARTEAM, new StarteamScmProvider());
	    scmManager.setScmProvider(TYPE_CLEARCASE, new ClearCaseScmProvider());
	    scmManager.setScmProvider(TYPE_SYNERGY, new SynergyScmProvider());
	    scmManager.setScmProvider(TYPE_VSS, new VssScmProvider());
	    scmManager.setScmProvider(TYPE_HG, new HgScmProvider());
	    
	    scmManager.setScmProvider(TYPE_GIT, new GitExeScmProvider());
	    
	    scmManager.setScmProvider(TYPE_BAZAAR, new BazaarScmProvider());
	    scmManager.setScmProvider(TYPE_ACCUREV, new AccuRevScmProvider());
	}

	public void start() {
		contentHasBeenChanged(null);
		commitContent(null, "test maven scm manager");
	}
	
	public void stop() {
		
	}
	
	@Override
	public boolean importContent(ContentItem contentItem, String revision) {
		
		return false;
	}
	
	@Override
	public boolean updateContent(ContentItem contentItem, String revision) {
		
		return false;
	}
	
	@Override
	public boolean revertContent(ContentItem contentItem) {
		
		return false;
	}
	
	@Override
	public boolean commitContent(ContentItem contentItem, String comment) {
		try {
			String repositoryUrl = "scm:hg:ssh://henry.imag.fr/cadseg";
			ScmRepository repository =
				scmManager.makeScmRepository(repositoryUrl);
			
			ScmFileSet fileSet = new ScmFileSet(new File("C:\\workspaces\\cadseg\\Model.workspace.CadseG"));
			CheckInScmResult statusResult = scmManager.checkIn(repository, fileSet, comment);
			
			return statusResult.isSuccess();
		} catch (ScmRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean contentHasBeenChanged(ContentItem contentItem) {
		try {
			ScmRepository repository =
				scmManager.makeScmRepository("scm:hg:ssh://henry.imag.fr/toto");
			
			
			ScmFileSet fileSet = new ScmFileSet(new File("C:\\workspaces\\cadseg\\Model.Workspace.CadseG"));
			StatusScmResult statusResult = scmManager.status(repository, fileSet);
			
			if (!statusResult.isSuccess())
				return false; //TODO should throw an exception
			
			List changedFiles = statusResult.getChangedFiles();
			if (changedFiles == null)
				return false;
			
			return !changedFiles.isEmpty();
		} catch (ScmRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
//	public void checkoutProject(MavenProjectScmInfo info, File location,
//		      IProgressMonitor monitor) throws CoreException {
//		    try {
//		      String repositoryUrl = info.getRepositoryUrl();
//		    
//		      ScmRepository repository =
//		scmManager.makeScmRepository(repositoryUrl);
//		      
//		      // ScmVersion version = new ScmRevision(info.getRevision());
//		      
//		      ScmFileSet fileSet = new ScmFileSet(location);
//		      
//		      ScmVersion scmVersion = getScmVersion(info);
//		      
//		      CheckOutScmResult scmResult = scmManager.checkOut(repository,
//		fileSet, scmVersion, true);
//		      
//		      MavenConsole console = MavenPlugin.getDefault().getConsole();
//		      
//		      String provider = repository.getProvider();
//		      
//		      if(!scmResult.isSuccess()) {
//		        console.logError(provider + " : failure");
//		        console.logError(provider + " : " + scmResult.getCommandLine());
//		        throw new CoreException(new Status(IStatus.ERROR,
//		MavenScmPlugin.PLUGIN_ID, -1, //
//		            scmResult.getCommandOutput() + " " +
//		scmResult.getProviderMessage(), null));
//		      }
//
//		      // String relativePathProjectDirectory =
//		scmResult.getRelativePathProjectDirectory();
//		      // List checkedOutFiles = scmResult.getCheckedOutFiles();
//		      // console.logMessage(provider + " : path  " +
//		relativePathProjectDirectory);
//		      // console.logMessage(provider + " : files " + checkedOutFiles);
//		      
//		    } catch (ScmException ex) {
//		      throw new CoreException(new Status(IStatus.ERROR,
//		MavenScmPlugin.PLUGIN_ID, -1, "Check out error", ex));
//		    }
//		  }
//	
//	/**
//	   * Handle provide specific SCM revision construction
//	   */
//	  private ScmVersion getScmVersion(MavenProjectScmInfo info) {
//	    String revision = info.getRevision();
//	    if(revision!=null && revision.trim().length()>0) {
//	      if(TYPE_CVS.equals(getType())) {
//	        // CVS tags
//	        return new ScmTag(revision.trim());
//	      } else if(TYPE_SVN.equals(getType())) {
//	        return new ScmRevision(revision);
//	      }
//	    }
//	    return null;
//	  }
	
}

