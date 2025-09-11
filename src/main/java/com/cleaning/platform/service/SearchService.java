package com.cleaning.platform.service;

import com.cleaning.platform.domain.Post;
import com.cleaning.platform.domain.ServiceProvider;
import com.cleaning.platform.repository.PostRepository;
import com.cleaning.platform.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final PostRepository postRepository;
    private final ServiceProviderRepository providerRepository;

    public List<Post> searchPosts(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }
    public List<ServiceProvider> searchProviders(String keyword) {
        return providerRepository.findByProviderNameContaining(keyword);
    }
}