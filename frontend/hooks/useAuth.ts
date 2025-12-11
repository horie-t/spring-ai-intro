// hooks/useAuth.ts (Next.js/React)
import useSWR from 'swr'; // SWRなどのデータ取得ライブラリを使用

const fetcher = (url: string) => fetch(url, {credentials: 'include',}).then(res => res.json());

export function useAuth() {
    const { data: user, error, isLoading } = useSWR('http://localhost:8080/api/user/me', fetcher);
    const isAuthenticated = !!user && !error;

    return {
        user: isAuthenticated ? user : null,
        isLoading,
        isAuthenticated,
    };
}
