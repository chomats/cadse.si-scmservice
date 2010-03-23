package fr.imag.adele.cadse.si.scm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.starteam.StarteamScmProvider;
import org.apache.maven.scm.provider.synergy.SynergyScmProvider;
import org.apache.maven.scm.provider.vss.VssScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import fr.imag.adele.cadse.as.scm.SCMException;
import fr.imag.adele.cadse.as.scm.SCMRevision;
import fr.imag.adele.cadse.as.scm.SCMService;
import fr.imag.adele.cadse.core.content.ContentItem;

public class SCMServiceImpl implements SCMService {

	private static final String CANNOT_RETRIEVE_SCM_URL = "Cannot retrieve scm url";
	private static final String SVN_URL_INFO_PROP = "URL";
	private static final String SVN_REV_INFO_PROP = "R.vision";
	private static final String SVN_INFO_PROP_DELIMITER = " : ";
	private static final String SVN_INFO_PROP_POSTFIX_PATTERN = ".*";
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

	private BasicScmManager _scmManager;
	private SvnExecScmProviderWithRepoURL _svnProvider;
	
	public SCMServiceImpl() {
	    _scmManager = new BasicScmManager();
	    
	    _scmManager.setScmProvider(TYPE_CVS, new CvsJavaScmProvider());
	    // scmManager.setScmProvider("cvs", new CvsExeScmProvider());
	    _svnProvider = new SvnExecScmProviderWithRepoURL();
		_scmManager.setScmProvider(TYPE_SVN, _svnProvider);

	    _scmManager.setScmProvider(TYPE_PERFORCE, new PerforceScmProvider());
	    _scmManager.setScmProvider(TYPE_STARTEAM, new StarteamScmProvider());
	    _scmManager.setScmProvider(TYPE_CLEARCASE, new ClearCaseScmProvider());
	    _scmManager.setScmProvider(TYPE_SYNERGY, new SynergyScmProvider());
	    _scmManager.setScmProvider(TYPE_VSS, new VssScmProvider());
	    _scmManager.setScmProvider(TYPE_HG, new HgScmProvider());
	    
	    _scmManager.setScmProvider(TYPE_GIT, new GitExeScmProvider());
	    
	    _scmManager.setScmProvider(TYPE_BAZAAR, new BazaarScmProvider());
	    _scmManager.setScmProvider(TYPE_ACCUREV, new AccuRevScmProvider());
	}

	public void start() {
		// do nothing
	}
	
	public void stop() {
		// do nothing
	}
	
	@Override
	public SCMRevision importContent(ContentItem contentItem, String scmRevision) throws SCMException {
		try {
			String scmRepoUrl = getSCMRepositoryURL(contentItem);
			if (scmRepoUrl == null)
				throw new SCMException(CANNOT_RETRIEVE_SCM_URL);
			ScmRepository repository =
				_scmManager.makeScmRepository(scmRepoUrl);
			
			ScmFileSet fileSet = new ScmFileSet(new File(getFilePath(contentItem)), "**.*"); // TODO manage unique file
			CheckOutScmResult checkoutResult = _scmManager.checkOut(repository, fileSet, 
					getScmVersion(scmRevision, repository.getProvider()), true);
			
			boolean isSuccess = checkoutResult.isSuccess();
			if (!isSuccess)
				return null;
			
			return new SCMRevision(scmRevision, scmRepoUrl);
		} catch (ScmRepositoryException e) {
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			e.printStackTrace();
		} catch (ScmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public SCMRevision updateContent(ContentItem contentItem, String scmRevision) throws SCMException {
		try {
			String scmRepoUrl = getSCMRepositoryURL(contentItem);
			if (scmRepoUrl == null)
				throw new SCMException(CANNOT_RETRIEVE_SCM_URL);
			ScmRepository repository =
				_scmManager.makeScmRepository(scmRepoUrl);
			
			ScmFileSet fileSet = new ScmFileSet(new File(getFilePath(contentItem)), "**.*"); // TODO manage unique file
			UpdateScmResult updateResult = _scmManager.update(repository, fileSet, 
					getScmVersion(scmRevision, repository.getProvider()));
			
			boolean isSuccess = updateResult.isSuccess();
			if (!isSuccess)
				return null;
			
			return new SCMRevision(scmRevision, scmRepoUrl);
		} catch (ScmRepositoryException e) {
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			e.printStackTrace();
		} catch (ScmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public SCMRevision revertContent(ContentItem contentItem) throws SCMException {
		String scmRevision = contentItem.getSCMRevision();
		return updateContent(contentItem, scmRevision);
	}
	
	@Override
	public SCMRevision commitContent(ContentItem contentItem, String comment) throws SCMException {
		try {
			String scmRepoUrl = getSCMRepositoryURL(contentItem);
			if (scmRepoUrl == null)
				throw new SCMException(CANNOT_RETRIEVE_SCM_URL);
			ScmRepository repository =
				_scmManager.makeScmRepository(scmRepoUrl);
			
			String filePath = getFilePath(contentItem);
			ScmFileSet fileSet = new ScmFileSet(new File(filePath)); // TODO manage unique file
//			AddScmResult addResult = _scmManager.add(repository, fileSet, comment);
			
			CheckInScmResult checkInResult = _scmManager.checkIn(repository, fileSet, comment);
			boolean isSuccess = checkInResult.isSuccess();
			if (!isSuccess)
				throw new SCMException("Cannot commit " + filePath + " : " + checkInResult.getCommandOutput());
			
			String scmRevision = getScmRevision(filePath);
			if (scmRevision == null)
				throw new SCMException("Cannot retireve scm revision.");
			return new SCMRevision(scmRevision, scmRepoUrl);
		} catch (ScmRepositoryException e) {
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			e.printStackTrace();
		} catch (ScmException e) {
			e.printStackTrace();
		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return null;
	}

	@Override
	public boolean contentHasBeenChanged(ContentItem contentItem) throws SCMException {
		try {
			String scmRepoUrl = getSCMRepositoryURL(contentItem);
			if (scmRepoUrl == null)
				throw new SCMException(CANNOT_RETRIEVE_SCM_URL);
			ScmRepository repository = _scmManager.makeScmRepository(scmRepoUrl);
			
			ScmFileSet fileSet = new ScmFileSet(new File(getFilePath(contentItem)));
			StatusScmResult statusResult = _scmManager.status(repository, fileSet);
			
			if (!statusResult.isSuccess())
				throw new SCMException("Status command failed");
			
			List changedFiles = statusResult.getChangedFiles();
			if (changedFiles == null)
				return false;
			
			return !changedFiles.isEmpty();
		} catch (ScmRepositoryException e) {
			e.printStackTrace();
		} catch (NoSuchScmProviderException e) {
			e.printStackTrace();
		} catch (ScmException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public String getSCMRepositoryURL(ContentItem contentItem) throws SCMException {
		String scmRepoUrl = contentItem.getSCMRepoUrl();
		if ((scmRepoUrl != null) && (!scmRepoUrl.trim().equals("")))
			return scmRepoUrl;
		
		String svnRepoUrl = getSvnRepoUrl(contentItem);
		if (svnRepoUrl == null)
			return null;
		
		return getSCMSvnRepositoryURL(svnRepoUrl);
	}

	private String getSCMSvnRepositoryURL(String svnRepoUrl) {
		return "scm:svn:" + svnRepoUrl;
	}
	
	private String getFilePath(ContentItem contentItem) {
		String filePath = null;
		IFile file = (IFile) contentItem.getMainMappingContent(IFile.class);
		if (file != null)
			filePath = file.getLocation().toPortableString();
		else {
			IFolder folder = (IFolder) contentItem.getMainMappingContent(IFolder.class);
			if (folder != null && folder.getLocation() != null)
				filePath = folder.getLocation().toPortableString();
			else {
				IProject project = (IProject) contentItem.getMainMappingContent(IProject.class);
				if (project != null && project.getLocation() != null)
					filePath = project.getLocation().toPortableString();
			}
		} 
		
		return filePath;
	}

	/**
	 * Returns repository SVN url related to specified content item. 
	 * 
	 * @param contentItem item representing content of a logical item
	 * @return repository SVN url related to specified content item. 
	 */
	private String getSvnRepoUrl(ContentItem contentItem) {
		String filePath = getFilePath(contentItem);
		if (filePath == null)
			return null;
		String svnRepoUrl = getSvnInfoProp(SVN_URL_INFO_PROP, filePath);
		
//		ScmFileSet fileSet = new ScmFileSet(new File(filePath));
//		SvnInfoCommand infoCmd = (SvnInfoCommand) _svnProvider.getInfoCommand();
//		String svnUrl = null;
//		SvnScmProviderRepository scmRepo = new SvnScmProviderRepository("svn://test");
//		try {
//			SvnInfoScmResult result = infoCmd.executeInfoCommand(scmRepo, fileSet, null, false, "HEAD");
//			if (result.isSuccess()) {
//				for (Object item : result.getInfoItems()) {
//					//TODO
//				}
//			}
//		} catch (ScmException e) {
//			e.printStackTrace();
//		}
		
		return svnRepoUrl;
	}

	private String getSvnInfoProp(String prop, String filePath) {
		String svnInfoProp = null;
		BufferedReader inputReader = null;
		try {
		    // Execute a command with an argument that contains a space
		    String[] commands = new String[]{"svn", "info", filePath};
		          
		    Process p = Runtime.getRuntime().exec(commands);
		    
		    // Read from an input stream
	        InputStream in = p.getInputStream();
	        inputReader = new BufferedReader(new InputStreamReader(in));

	        String line;
	        String prefix = prop + SVN_INFO_PROP_DELIMITER;
	        Pattern pattern = Pattern.compile(prefix + SVN_INFO_PROP_POSTFIX_PATTERN);
	        while ((line = inputReader.readLine()) != null) {
	        	Matcher m = pattern.matcher(line);
				if (!m.matches())
	            	continue;
	            
				svnInfoProp = line.substring(prefix.length());
	            break;
	        }
	        inputReader.close();
	        inputReader = null;
	        
	        p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (inputReader != null)
				try {
					inputReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return svnInfoProp;
	}
	
	private String getScmRevision(String filePath) {
		return getSvnInfoProp(SVN_REV_INFO_PROP, filePath);
	}

	/**
	 * Handle provide specific SCM revision construction
	 */
	private ScmVersion getScmVersion(String revision, String scmType) {
		if (revision != null && revision.trim().length() > 0) {
			if (TYPE_CVS.equals(scmType)) {
				// CVS tags
				return new ScmTag(revision.trim());
			} else if (TYPE_SVN.equals(scmType)) {
				return new ScmRevision(revision);
			}
			// else if(TYPE_HG.equals(getType())) {
			// return new HgRevision(revision);
			// }
		}
		return null;
	}
	
}

