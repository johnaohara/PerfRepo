package org.perfrepo.web.adapter;

import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.test.TestSearchCriteria;

import java.util.List;

/**
 * Service adapter for test definitions. Adapter provides operations for {@link TestDto} object.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface TestAdapter {
    /**
     * Return {@link TestDto} object by its id.
     *
     * @param id The test identifier.
     *
     * @return The found {@link TestDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     */
    TestDto getTest(Long id);

    /**
     * Return {@link TestDto} object by its uid.
     *
     * @param uid The test string unique identifier.
     *
     * @return The found {@link TestDto} object.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     */
    TestDto getTest(String uid);

    /**
     * Create new {@link TestDto} object.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Name of test</li>
     *  <li><strong>uid</strong> - Uid of test</li>
     *  <li><strong>description</strong> - Description of test</li>
     *  <li><strong>group</strong> - Group of test</li>
     * <ul/>
     *
     * @param testDto Parameters of the test that will be created.
     * @return Created {@link TestDto} object.
     */
    TestDto createTest(TestDto testDto);

    /**
     * Update the {@link TestDto} object.
     *
     * <br>
     * <strong>Validation fields:</strong>
     * <ul>
     *  <li><strong>name</strong> - Name of test</li>
     *  <li><strong>uid</strong> - Uid of test</li>
     *  <li><strong>description</strong> - Description of test</li>
     *  <li><strong>group</strong> - Group of test</li>
     * <ul/>
     *
     * @param testDto Parameters of the test that will be updated.
     * @return Updated {@link TestDto} object.
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     */
    TestDto updateTest(TestDto testDto);

    /**
     * Remove the {@link TestDto} object.
     *
     * @param id The test identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.NotFoundException If the object does not exist.
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     */
    void removeTest(Long id);

    /**
     * Return all tests.
     *
     * @return List of all tests.
     */
    List<TestDto> getAllTests();

    /**
     * Return all {@link TestDto} tests that satisfy search conditions.
     *
     * @param searchParams The test search criteria params.
     *
     * @return List of {@link TestDto} tests.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     */
    SearchResult<TestDto> searchTests(TestSearchCriteria searchParams);

    /**
     * Return saved test search params.
     *
     * @return Search criteria params.
     */
    TestSearchCriteria getSearchCriteria();

    /**
     * Return true if logged user subscribes the test alerts.
     *
     * @param testId The test identifier.
     *
     * @return True if logged user subscribes the alerts of the test.
     */
    boolean isSubscriber(Long testId);

    /**
     * Subscribe the logged user to the test alerts.
     *
     * @param testId Test {@link TestDto} identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     */
    void addSubscriber(Long testId);

    /**
     * Unsubscribe the logged user from the test alerts.
     *
     * @param testId Test {@link TestDto} identifier.
     *
     * @throws org.perfrepo.web.adapter.exceptions.BadRequestException If the request is bad.
     */
    void removeSubscriber(Long testId);
}
