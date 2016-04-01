package com.idyria.osi.tea.compile

import org.scalatest.GivenWhenThen
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import com.idyria.osi.tea.file.DirectoryUtilities
import java.io.File
import java.net.URLClassLoader

class CompileClassDomainTest  extends FunSuite with GivenWhenThen with BeforeAndAfter  {
  
  
  val sourceFolder = new File("src/test/resources/compile")
  val outputFolder = new File("target/tco")

  before {
    outputFolder.mkdirs()
    DirectoryUtilities.deleteDirectoryContent(outputFolder)
  }
  
  /**
   * 
   */
  test("Compile a new simple class, enforce current thread updating") {
    
    //-- Create Compiler
    var idCompiler = new IDCompiler
    idCompiler.addSourceOutputFolders(sourceFolder -> outputFolder)

    //-- Compile Files
    var sourceFile = new File(sourceFolder, "TestCompile.scala").getAbsoluteFile
    idCompiler.compileFile(sourceFile)
    
    //-- Load class
    var initClassLoader = Thread.currentThread().getContextClassLoader
    Given("A correct classloader, load the class")
    val className = "com.idyria.osi.tea.compile.TestCompile"
    val loadClassDomain = new ClassDomain(Array(outputFolder.toURI().toURL()), Thread.currentThread().getContextClassLoader)
    Thread.currentThread().setContextClassLoader(loadClassDomain)
    var cl = Thread.currentThread().getContextClassLoader.loadClass(className)
    println(s"${cl.getClassLoader.isInstanceOf[ClassDomain]}")
    
    
    //-- Then Go Back 
    When("Reset Classloader to initial state")
    var classdomainSupport = new ClassDomainSupport {
      
    }
    Then("get classloader should determine usage of previous Class Domain")
    var resClassloader = classdomainSupport.getClassLoaderFor(cl)
    assertResult(loadClassDomain)(resClassloader)
    
    //-- Now instantiate class with enforced classloader
    And("Instantiating should work with no typing")
    var obj = classdomainSupport.instanceOfClass[Object](cl)
    
    And("also with parent typing")
    var obj2 = classdomainSupport.instanceOfClass[TestCompileParent](cl)
  }
  
  /**
   * 
   */
  test("Enforce Compiler Output in Classloader") {
    
    //-- Create Compiler
    var idCompiler = new IDCompiler
    idCompiler.addSourceOutputFolders(sourceFolder -> outputFolder)

    //-- Compile Files
    var sourceFile = new File(sourceFolder, "TestCompile.scala").getAbsoluteFile
    idCompiler.compileFile(sourceFile)
    
    //-- Enforce Classloader output, and load class based on this
    Given("The Class has been compiled")
    Then("Use the withURLInClassLoader function to ensure Compiler Output is in classloader")
    var classdomainSupport = new ClassDomainSupport {
      
    }
    classdomainSupport.withURLInClassloader(outputFolder.toURI().toURL()) {
      val className = "com.idyria.osi.tea.compile.TestCompile"
      Thread.currentThread().getContextClassLoader.loadClass(className).newInstance()
    }
    
    
  }
  
  
}