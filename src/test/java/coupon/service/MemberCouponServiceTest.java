package coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import coupon.domain.Category;
import coupon.domain.Coupon;
import coupon.domain.Member;
import coupon.domain.MemberCoupon;
import coupon.exception.CouponException;
import coupon.repository.CouponRepository;
import coupon.repository.MemberCouponRepository;
import coupon.repository.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberCouponServiceTest {

    @Autowired
    MemberCouponService memberCouponService;
    @Autowired
    MemberCouponRepository memberCouponRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CouponRepository couponRepository;

    @Disabled // 지연 복제로 인해서 테스트 실패
    @Test
    @DisplayName("사용자의 쿠폰을 조회한다.")
    void getCoupons() {
        Member member = new Member();
        Coupon coupon = new Coupon("coupon", BigDecimal.valueOf(1_000), BigDecimal.valueOf(10_000),
                Category.FOOD, LocalDate.now(), LocalDate.now());
        Member saveMember = memberRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);
        memberCouponRepository.save(new MemberCoupon(saveCoupon.getId(), saveMember));

        List<Coupon> coupons = memberCouponService.getCoupons(saveMember.getId());

        assertThat(coupons).hasSize(1);
    }

    @Test
    @DisplayName("쿠폰을 발급한다.")
    void issueCoupon() {
        Member member = new Member();
        Coupon coupon = new Coupon("coupon", BigDecimal.valueOf(1_000), BigDecimal.valueOf(10_000),
                Category.FOOD, LocalDate.now(), LocalDate.now());
        Member saveMember = memberRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        Long savedId = memberCouponService.issueCoupon(saveMember.getId(), saveCoupon.getId()).getId();

        MemberCoupon actual = memberCouponRepository.findByIdImmediately(savedId).orElseThrow();
        assertAll(
                () -> assertThat(actual.getCouponId()).isEqualTo(coupon.getId()),
                () -> assertThat(actual.getMember()).isEqualTo(saveMember)
        );
    }

    @Test
    @DisplayName("같은 쿠폰을 5장 이상 발급하면 예외가 발생한다.")
    void issueCouponWhenExceedMaxCouponCount() {
        Member member = new Member();
        Coupon coupon = new Coupon("coupon", BigDecimal.valueOf(1_000), BigDecimal.valueOf(10_000),
                Category.FOOD, LocalDate.now(), LocalDate.now());
        Member saveMember = memberRepository.save(member);
        Coupon saveCoupon = couponRepository.save(coupon);

        IntStream.range(0, 5)
                .forEach(i -> memberCouponService.issueCoupon(saveMember.getId(), saveCoupon.getId()));

        assertThatThrownBy(() -> memberCouponService.issueCoupon(saveMember.getId(), saveCoupon.getId()))
                .isInstanceOf(CouponException.class)
                .hasMessage("같은 쿠폰은 최대 5장까지 발급 가능합니다.");
    }
}
