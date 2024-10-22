package coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long couponId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean used;

    private LocalDate usedDate;

    private LocalDate expiredDate;

    protected MemberCoupon() {
    }

    public MemberCoupon(Long couponId, Member member) {
        this(null, couponId, member);
    }

    private MemberCoupon(Long id, Long couponId, Member member) {
        this.id = id;
        this.couponId = couponId;
        this.member = member;
        this.used = false;
        this.usedDate = null;
        this.expiredDate = LocalDate.now().plusDays(7);
    }


    public Long getId() {
        return id;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Member getMember() {
        return member;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }
}
