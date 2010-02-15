package fr.imag.adele.cadse.si.scm;

import java.io.File;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;

import org.apache.maven.scm.ScmException;

public class SvnExecScmProviderWithRepoURL extends SvnExeScmProvider {

	public SvnExecScmProviderWithRepoURL() {
		// do nothing
	}
	
	@Override
	public String getRepositoryURL(File path) throws ScmException {
		return super.getRepositoryURL(path);
	}
}
