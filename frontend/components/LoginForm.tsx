'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '@/components/ui/card';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { useRouter } from 'next/navigation'; // Next.js App Routerの場合


// --- フォームのスキーマ定義 (バリデーションルール) ---
const formSchema = z.object({
    username: z.string().min(3, {
        message: 'ユーザー名は3文字以上で入力してください。',
    }),
    password: z.string().min(6, {
        message: 'パスワードは6文字以上で入力してください。',
    }),
});

type FormSchemaType = z.infer<typeof formSchema>;

/**
 * Spring Security用のログインフォームコンポーネント
 */
export function LoginForm() {
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const form = useForm<FormSchemaType>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: '',
            password: '',
        },
    });

    const router = useRouter();

    const onSubmit = async (data: FormSchemaType) => {
        setError(null);
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    username: data.username,
                    password: data.password,
                }),
                credentials: 'include',
            });

            if (response.ok) {
                console.log('認証に成功しました。');
                router.push('/');
            } else if (response.status === 401) {
                setError('ユーザー名またはパスワードが正しくありません。');
            } else {
                setError(`認証中に予期せぬエラーが発生しました: ${response.status}`);
            }
        } catch (err) {
            console.error('通信エラー:', err);
            setError('サーバーとの通信中にエラーが発生しました。');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex justify-center items-center min-h-screen bg-gray-50">
            <Card className="w-[380px] shadow-lg">
                <CardHeader>
                    <CardTitle className="text-2xl text-center">ログイン</CardTitle>
                </CardHeader>
                <CardContent>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                            {/* 1. ユーザー名 (Username) フィールド */}
                            <FormField
                                control={form.control}
                                name="username"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>ユーザー名</FormLabel>
                                        <FormControl>
                                            <Input placeholder="user123" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            {/* 2. パスワード (Password) フィールド */}
                            <FormField
                                control={form.control}
                                name="password"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>パスワード</FormLabel>
                                        <FormControl>
                                            <Input type="password" placeholder="••••••••" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            {/* 3. 認証エラーメッセージの表示 */}
                            {error && (
                                <p className="text-sm text-red-600 font-medium text-center">
                                    {error}
                                </p>
                            )}

                            {/* 4. フォーム送信ボタン */}
                            <Button type="submit" className="w-full mt-6" disabled={loading}>
                                {loading ? '認証中...' : 'サインイン'}
                            </Button>
                        </form>
                    </Form>
                </CardContent>
                <CardFooter className="text-xs text-center text-gray-500 justify-center">
                    デモユーザー: user123 / pass1234
                </CardFooter>
            </Card>
        </div>
    );
}
