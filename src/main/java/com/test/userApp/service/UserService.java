package com.test.userApp.service;

import com.test.userApp.Address;
import com.test.userApp.entity.User;
import com.test.userApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private List<String> output = new ArrayList<>();


    public User saveUser(User user) {
        user.setRegistrationDate(new Date());
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Page<User> searchUsers(String searchBy, Pageable pageable) {
        return userRepository.findAll(searchByCriteria(searchBy), pageable);
    }

    public List<String> getPermutations(String input){
        int length = input.length();
        return permute(input, 0, length - 1);
    }

    private List<String> permute(String string, int start, int end)
    {
        if (start == end) {
            output.add(string);
            System.out.println(string);
        }
        else {
            for (int i = start; i <= end; i++) {
                string = swap(string, start, i);
                permute(string, start + 1, end);
                string = swap(string, start, i);
            }
        }
        return output;
    }

    public String swap(String a, int i, int j)
    {
        char temp;
        char[] charArray = a.toCharArray();
        temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;
        return String.valueOf(charArray);
    }

    private Specification<User> searchByCriteria(String searchBy) {
        return ((root, query, criteriaBuilder) -> {
            Join<Address, User> address = root.join("addresses");
            List<Predicate> predicates = new ArrayList<>();
            search(root, criteriaBuilder, predicates, searchBy);
            searchByPincode(address, criteriaBuilder, predicates, searchBy);
            return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

    private void search(Root<User> user, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String searchBy) {
        if (Objects.nonNull(searchBy)) {
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(
                                    criteriaBuilder.upper(user.get("name"))
                                    , "%" + searchBy.toUpperCase() + "%"

                            )
                    )
            );
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(
                                    (user.get("registrationDate").as(String.class))
                                    , "%" + searchBy + "%"

                            )
                    )
            );
        }
    }

    private void searchByPincode(Join<Address, User> address, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String searchBy) {
        if (Objects.nonNull(searchBy)) {
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.like(
                                    (address.get("pincode").as(String.class))
                                    , "%" + searchBy + "%"
                            )
                    )
            );
        }
    }
}
