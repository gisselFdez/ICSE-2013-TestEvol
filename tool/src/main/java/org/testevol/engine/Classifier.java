package org.testevol.engine;

import japa.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testevol.domain.Version;
import org.testevol.engine.diff.Pair;
import org.testevol.engine.domain.Category;
import org.testevol.engine.domain.CategoryC1;
import org.testevol.engine.domain.CategoryC2;
import org.testevol.engine.domain.CategoryC3;
import org.testevol.engine.domain.CategoryC4;
import org.testevol.engine.domain.CategoryC5;
import org.testevol.engine.domain.CategoryC6;
import org.testevol.engine.domain.CategoryC7;
import org.testevol.engine.domain.CategoryC8;
import org.testevol.engine.domain.CloneAnalyser;
import org.testevol.engine.domain.Results;
import org.testevol.engine.domain.TestEvolLog;
import org.testevol.engine.domain.VersionPair;
import org.testevol.engine.driver.CopyAndPasteDetectorDriver;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.ClassHierarchyException;

public class Classifier extends Task {

  private Differ differ;
  private Map<VersionPair, Results> results; // Les resultats pour chaque paire de versions

  public Classifier(List<Version> versions, Differ differ, TestEvolLog log) {
    super(versions, log);
    this.differ = differ;
    this.results = new HashMap<VersionPair, Results>();

  }

  @Override
  /*
   * Point d'entrée du Classifier
   * 
   * @see org.testevol.engine.Task#go()
   */
  public boolean go() throws Exception {
    Version oldVersion = null;

    log.logStrong("Starting classifier...");

    // Pour chaque version
    for (Version version : versions) {
      // on ne classifie pas les resultats du differ pour uniquement la v1, il faut une paire de versions
      if (oldVersion == null) {
        oldVersion = version;
        log.log("Skipping first version: " + version.getName());
        continue;
      }

      // on a une paire de versions, on peut commencer le classifier
      log.log("Classifing tests from pair " + oldVersion.getName() + " - " + version.getName());

      // la paire de versions
      VersionPair versionPair = new VersionPair(oldVersion, version);
      JavaDifferencer javaDiffTests = differ.getTestDifferencers().get(versionPair);
      results.put(versionPair, classify(versionPair, javaDiffTests));
      oldVersion = version;
    }
    return true;
  }

  /*
   * Retourne les resultats pour chaque paire de versions
   */
  public Map<VersionPair, Results> getResults() {
    return results;
  }

  /*
   * Construit les resultats pour une paire de versions
   */
  private Results classify(VersionPair versions, JavaDifferencer differencer) throws ClassHierarchyException, ParseException, IOException {

    // Les deux versions du programme
    Version oldVersion = versions.getOldVersion();
    Version version = versions.getVersion();

    // Les deux suites de tests associées aux versions
    Set<String> oldTests = oldVersion.getTestsList();
    Set<String> newTests = versions.getVersion().getTestsList();

    
    /* ********************************************************* Categories 1 & 2 ***********************************************************/
    
    CategoryC1 categoryC1 = new CategoryC1(oldVersion, version); // Tests en erreur (AE, CE, RE, Pass)
    CategoryC2 categoryC2 = new CategoryC2(oldVersion, version); // Anciens tests qui passent sur le nouveau programme

    for (Pair<IMethod> methodsPair : differencer.getModifiedMethods()) { // on parcourt toutes les méthodes qui ont été modifiées
      IMethod first = methodsPair.getFirst();
      String test = first.getSignature().toString();
      if (!oldTests.contains(test) || !newTests.contains(test)) { // si ce ne sont pas des méthodes de test alors on passe
        continue;
      }

      if (categoryC1.isACandidateTest(test)) {
        TestMethodDifferencer testMethodDifferencer = new TestMethodDifferencer(methodsPair.getFirst(), methodsPair.getSecond());
        categoryC1.confirmTestOnThisCategory(test, testMethodDifferencer.getChangeType());
      }

      if (categoryC2.isACandidateTest(test)) {
        categoryC2.confirmTestOnThisCategory(test);
      }
    }

    /* ********************************************************* Categories 3, 4 & 5 ***********************************************************/
    
    CategoryC3 categoryC3 = new CategoryC3(oldVersion, version); // Tests supprimés en AE, RE
    CategoryC4 categoryC4 = new CategoryC4(oldVersion, version); // Tests supprimés en CE
    CategoryC5 categoryC5 = new CategoryC5(oldVersion, version); // Tests supprimés qui passent

    Collection<IMethod> deletedMethods = differencer.getDeletedMethods();

    for (IClass deletedClazz : differencer.getDeletedClasses()) {
      deletedMethods.addAll(deletedClazz.getAllMethods());
    }

    for (IMethod method : deletedMethods) { // pour toutes les méthodes supprimées

      String test = method.getSignature().toString();

      if (oldTests.contains(test) && !newTests.contains(test)) { // on catégorise uniquement les méthodes de tests
        if (categoryC3.isACandidateTest(test)) {
          categoryC3.confirmTestOnThisCategory(test);
        }

        if (categoryC4.isACandidateTest(test)) {
          categoryC4.confirmTestOnThisCategory(test);
        }

        if (categoryC5.isACandidateTest(test)) {
          categoryC5.confirmTestOnThisCategory(test);
        }
      }
    }
    
    /* ********************************************************* Categories 6, 7 & 8 ***********************************************************/
    
    CategoryC6 categoryC6 = new CategoryC6(oldVersion, version); // Tests ajoutés en AE, RE
    CategoryC7 categoryC7 = new CategoryC7(oldVersion, version); // Tests ajoutés en CE
    CategoryC8 categoryC8 = new CategoryC8(oldVersion, version); // Tests ajoutés qui passent

    Collection<IMethod> addedMethods = differencer.getAddedMethods();

    for (IClass addedClazz : differencer.getAddedClasses()) { // on récupère aussi les méthodes qui se trouvent dans les nouvelles classes
      addedMethods.addAll(addedClazz.getAllMethods());
    }

    for (IMethod method : addedMethods) { // pour chaque méthode ajoutée
      String test = method.getSignature().toString();

      if (!oldTests.contains(test) && newTests.contains(test)) { // on catégorise uniquement les méthodes de test
        if (categoryC6.isACandidateTest(method.getSignature().toString())) {
          categoryC6.confirmTestOnThisCategory(method.getSignature().toString());
        }
        if (categoryC7.isACandidateTest(method.getSignature().toString())) {
          categoryC7.confirmTestOnThisCategory(method.getSignature().toString());
        }
        if (categoryC8.isACandidateTest(method.getSignature().toString())) {
          categoryC8.confirmTestOnThisCategory(method.getSignature().toString());
        }
      }
    }

    // Construction des résultats à partir des catégories de tests
    Results results = new Results(categoryC1, categoryC2, categoryC3, categoryC4, categoryC5, categoryC6, categoryC7, categoryC8);

    searchClones(oldVersion, version, results);

    return results;
  }

  private void searchClones(Version oldVersion, Version version, Results results) throws ParseException, IOException {

    log.log("Running clone detection for pair " + oldVersion.getName() + " - " + version.getName());
    List<Category> categoriesToSearch = new ArrayList<Category>();

    categoriesToSearch.add(results.getCategoryC1());
    List<String> clonesOnCategory1 = performaCloneAnalysisCategory(oldVersion, version, results.getCategoryC1(), categoriesToSearch);

    categoriesToSearch.clear();
    categoriesToSearch.add(results.getCategoryC2());

    List<String> clonesOnCategory2 = performaCloneAnalysisCategory(oldVersion, version, results.getCategoryC2(), categoriesToSearch);

    categoriesToSearch.clear();
    categoriesToSearch.add(results.getCategoryC6());
    categoriesToSearch.add(results.getCategoryC7());
    categoriesToSearch.add(results.getCategoryC8());
    //
    List<String> clonesOnCategory3 = performaCloneAnalysisCategory(oldVersion, version, results.getCategoryC3(), categoriesToSearch);
    List<String> clonesOnCategory4 = performaCloneAnalysisCategory(oldVersion, version, results.getCategoryC4(), categoriesToSearch);
    List<String> clonesOnCategory5 = performaCloneAnalysisCategory(oldVersion, version, results.getCategoryC5(), categoriesToSearch);

    categoriesToSearch.clear();
    categoriesToSearch.add(results.getCategoryC3());
    categoriesToSearch.add(results.getCategoryC4());
    categoriesToSearch.add(results.getCategoryC5());

    List<String> clonesOnCategory6 = performaCloneAnalysisCategory(version, oldVersion, results.getCategoryC6(), categoriesToSearch);
    List<String> clonesOnCategory7 = performaCloneAnalysisCategory(version, oldVersion, results.getCategoryC7(), categoriesToSearch);
    List<String> clonesOnCategory8 = performaCloneAnalysisCategory(version, oldVersion, results.getCategoryC8(), categoriesToSearch);

    int totalNumberOfClonesFound = clonesOnCategory1.size() + clonesOnCategory2.size() + clonesOnCategory3.size() +
      clonesOnCategory4.size() + clonesOnCategory5.size() + clonesOnCategory6.size() +
      clonesOnCategory7.size() + clonesOnCategory8.size();

    if (totalNumberOfClonesFound == 0) {
      log.log("No clones found");
    } else {
      log.log(totalNumberOfClonesFound + " clones found");
    }

    results.getCategoryC1().removeTestsFromThisCategory(clonesOnCategory1);
    results.getCategoryC2().removeTestsFromThisCategory(clonesOnCategory2);
    results.getCategoryC3().removeTestsFromThisCategory(clonesOnCategory3);
    results.getCategoryC4().removeTestsFromThisCategory(clonesOnCategory4);
    results.getCategoryC5().removeTestsFromThisCategory(clonesOnCategory5);
    results.getCategoryC6().removeTestsFromThisCategory(clonesOnCategory6);
    results.getCategoryC7().removeTestsFromThisCategory(clonesOnCategory7);
    results.getCategoryC8().removeTestsFromThisCategory(clonesOnCategory8);

  }

  private List<String> performaCloneAnalysisCategory(Version subjectVersion,
    Version otherVersion,
    Category subject,
    List<Category> categoriesToSearch) throws ParseException, IOException {

    if (subject.getTestsOnThisCategory().isEmpty()) {
      return Collections.emptyList();
    }

    CloneAnalyser cloneAnalyserOld = new CloneAnalyser("SubjectVersionFile", subjectVersion);
    cloneAnalyserOld.addTests(subject.getTestsOnThisCategory());
    File subjectFile = cloneAnalyserOld.createFile();

    CloneAnalyser cloneAnalyserNew = new CloneAnalyser("OtherVersionFile", otherVersion);
    for (Category category : categoriesToSearch) {
      cloneAnalyserNew.addTests(category.getTestsOnThisCategory());
    }
    File otherVersionFile = cloneAnalyserNew.createFile();

    CopyAndPasteDetectorDriver copyAndPasteDetectorDriver = new CopyAndPasteDetectorDriver(10, subjectFile, otherVersionFile);
    copyAndPasteDetectorDriver.run();

    return copyAndPasteDetectorDriver.getClonesInFile1();

  }
}
